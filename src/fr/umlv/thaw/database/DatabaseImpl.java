package fr.umlv.thaw.database;


import fr.umlv.thaw.channel.Channel;
import fr.umlv.thaw.channel.ChannelFactory;
import fr.umlv.thaw.message.Message;
import fr.umlv.thaw.message.MessageFactory;
import fr.umlv.thaw.user.humanUser.HumanUser;
import fr.umlv.thaw.user.humanUser.HumanUserFactory;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static fr.umlv.thaw.database.DatabaseTools.*;

/**
 * This class represent an implementation of a Database
 * with the SQLITE driver
 */
public class DatabaseImpl implements Database {

    private final Connection co;
    private final Statement state;
    private PreparedStatement prep;


    /**
     * Construct a representation of our database.
     * The connection is already managed don't forget to close the Database
     *
     * @param pathToDB the path in which the database will be loaded / created
     * @param dbName   the file name of the database without the .db extension
     * @throws ClassNotFoundException if we cannot find the SQLITE library
     * @throws SQLException           if an error occurs during the creation of the database
     */
    DatabaseImpl(Path pathToDB, String dbName) throws ClassNotFoundException, SQLException {
        Objects.requireNonNull(pathToDB);
        Objects.requireNonNull(dbName);
        Class.forName("org.sqlite.JDBC");
        String forGetConnection = "jdbc:sqlite:" + pathToDB + FileSystems.getDefault().getSeparator() + dbName + ".db";
        co = DriverManager.getConnection(forGetConnection);
        Objects.requireNonNull(co);
        state = co.createStatement();
    }


    /*
    * Public's method
    * */

    @Override
    public void initializeDB() throws SQLException {
        final String query = createUsersTableRequest();
        exeUpda(query, state);
        createChannelsTable(state);
        createChanViewerTable(state);
    }


    @Override
    public void createLogin(HumanUser humanUser) throws NoSuchAlgorithmException, SQLException {
        Objects.requireNonNull(humanUser);
        String login = humanUser.getName();
        String cryptPass = humanUser.getPasswordHash();
        final String query = prepareInsertTwoValuesIntoTable("users");
        prep = co.prepareStatement(query);
        insertTwoValIntoTable(login, cryptPass, prep);
        executeRegisteredTask(co, prep);
    }

    @Override
    /*
    * For a security reason and to not make useless one line function, we create
    * ourselves the request here. We also avoid the fact that the function
    * can throw a SQLException if the channel already exist.
    * */
    public void createChannelTable(String channelName, String owner) throws SQLException {
        Objects.requireNonNull(channelName);
        Objects.requireNonNull(owner);
        try {
            prep = co.prepareStatement(String.format("create table if not exists '%s' (" +
                    "DATE INTEGER NOT NULL, " +
                    "MESSAGE TEXT NOT NULL, " +
                    "AUTHOR TEXT NOT NULL );", channelName));
            prep.executeUpdate();

        } catch (SQLException sql) {
            System.err.println("Table " + channelName + " already exist");
            return;
        }
        updateChannelsTable(channelName, owner, co);
        updateChanViewerTable(channelName, owner, co);
    }

    @Override
    public void addUserToChan(String channel, String toAuthorized, String authority) throws SQLException {
        Objects.requireNonNull(channel);
        Objects.requireNonNull(toAuthorized);
        Objects.requireNonNull(authority);
        if (userCanControlAccessToChan(channel, authority, co) && !canUserViewChannel(channel, toAuthorized, co)) {
            updateChanViewerTable(channel, toAuthorized, co);
        }
    }

    @Override
    /*
    * Because we must distinct each two cases (remove a user that is not the owner and remove the owner),
     * wa can't really simplify that much the function and if we externalize the SQL request, we could hide
     * the possible SQL Injection breach from FindBug (even if we got the control from the data).
     * For the second case, because we must remove each users from the channels, we must find every
     * user from a channel and remove them one by one before removing the channel entry in the channels table.
    * */
    public void removeUserAccessToChan(String channelName, String userNametoKick, String authorityName) throws SQLException {
        Objects.requireNonNull(channelName);
        Objects.requireNonNull(userNametoKick);
        Objects.requireNonNull(authorityName);
        if (userCanControlAccessToChan(channelName, authorityName, co) && !userNametoKick.equals(authorityName)) {
            final String removeUserAccesToChanRequest = "DELETE FROM CHANVIEWER WHERE "
                    + "CHANNAME LIKE ?"
                    + " AND MEMBER LIKE ? ;";

            prep = co.prepareStatement(removeUserAccesToChanRequest);
            prep.setString(1, channelName);
            prep.executeUpdate();
        } else if (userCanControlAccessToChan(channelName, authorityName, co) && userNametoKick.equals(authorityName)) {
            List<HumanUser> toEject = getUsersListFromChan(channelName);
            final String removeUserAccesToChanRequest = "DELETE FROM CHANVIEWER WHERE "
                    + "CHANNAME LIKE ? "
                    + " AND MEMBER LIKE ? ;";
            prep = co.prepareStatement(removeUserAccesToChanRequest);
            for (HumanUser user : toEject) {
                prep.setString(1, channelName);
                prep.setString(2, user.getName());
                prep.executeUpdate();
            }
            final String removeChannelFromChannels = "DELETE FROM CHANNELS WHERE "
                    + "CHANNAME LIKE ?  "
                    + " AND OWNER LIKE ? ;";
            prep = co.prepareStatement(removeChannelFromChannels);
            prep.setString(1, channelName);
            prep.setString(2, userNametoKick);
            prep.executeUpdate();
            state.execute(String.format("DROP TABLE IF EXISTS %s", channelName));
            /*prep = co.prepareStatement(String.format("DROP TABLE IF EXISTS %s", channelName));
            prep.executeUpdate();*/

        }
    }

    @Override
    public void addMessageToChannelTable(String channelName, Message msg) throws SQLException {
        Objects.requireNonNull(channelName);
        Objects.requireNonNull(msg);
        if (canUserViewChannel(channelName, msg.getSender().getName(), co)) {
            prep = co.prepareStatement(String.format("insert into '%s' values (?, ?, ?)", channelName));
            insertDateMessageAuthor(msg.getDate(), msg.getContent(), msg.getSender().getName(), prep);
            executeRegisteredTask(co, prep);
        }
    }

    @Override
    /*  To avoid the multiplication of temporary object such as
    * hum,login and pswd, we must make 3 lines.
    *   We must also not forget that we should close our ResulSet
    * object after request, and because me need a login and a password
    * that we retrieve from a request, our loop must done 4 differents
    * operations.
    *   Finally, we could get a list of empty user from our database
    * and so must we return an emptyList to avoid any problem.
    *   After that, if we get a result the userList object can
    * can be returned without any trouble.
    * */
    public List<HumanUser> getAllUsersList() throws SQLException {
        ResultSet rs = executeQuery("select * from users", state);
        List<HumanUser> userList = new ArrayList<>();
        HumanUser hum;
        String login;
        String pswd;
        while (rs.next()) {
            login = rs.getString("LOGIN");
            pswd = rs.getString("PSWD");
            hum = HumanUserFactory.createHumanUser(login, pswd);
            userList.add(hum);
        }
        rs.close();
        if (userList.isEmpty()) {
            return Collections.emptyList();
        }
        return userList;
    }


    @Override
    /*
    *   Mostly for the same reason as the function above, we can't really
    * reduce the length of the method without taking any security risk or
    * even without forgetting to close one of our PreparedStatement object.
    *   And we can't really delegate more without making the code more
    * complex.
    * */
    public List<HumanUser> getUsersListFromChan(String channelName) throws SQLException {
        Objects.requireNonNull(channelName);
        final String query = "SELECT MEMBER FROM CHANVIEWER WHERE CHANNAME LIKE ? ;";
        try (PreparedStatement p2 = co.prepareStatement(query)) {
            p2.setString(1, channelName);
            final String request = "SELECT PSWD FROM users WHERE LOGIN LIKE ? ;";
            List<HumanUser> users = new ArrayList<>();
            HumanUser tmpUser;
            String name;
            prep = co.prepareStatement(request);
            if (p2.execute()) {
                try (ResultSet rs = p2.getResultSet()) {
                    name = rs.getString("MEMBER");
                    prep.setString(1, name);
                    if (prep.execute()) {
                        try (ResultSet tmp = prep.getResultSet()) {
                            tmpUser = HumanUserFactory.createHumanUser(name, tmp.getString("PSWD"));
                            users.add(tmpUser);
                        }
                    }
                }
            }
            if (users.isEmpty()) {
                return Collections.emptyList();
            }
            return users;
        }
    }


    @Override
    /*
    *   We need to close our local PreparedStatement with a
    * try-with-resources and because we must construct a List,
    * we must add and construct each message one by one.
    *   We must also not forget to find the password associated
    * with an user to construct our HumanUser and then the Message
    * associated with the HumanUser.
    * */
    public List<Message> getMessagesList(String channelName) throws SQLException {
        boolean hasResult;//useful to know if we have found a channel
        try (PreparedStatement p2 = co.prepareStatement(String.format("SELECT * FROM  \"%s\"", channelName))) {
            final String request = "SELECT PSWD FROM users WHERE LOGIN LIKE ? ;";//the SQL request to retrieve the encrypted pasword from a user
            List<Message> msgs = new ArrayList<>();
            HumanUser tmpUser;
            Message tmpMessage;
            prep = co.prepareStatement(request);
            hasResult = p2.execute();//if true we got a channel
            while (hasResult) {
                //while we found a channel
                try (ResultSet rs = p2.getResultSet()) {
                    while (rs.next()) {//we retrieve the messages one by one
                        String author = rs.getString("AUTHOR");
                        String message = rs.getString("MESSAGE");
                        long date = rs.getLong("DATE");

                        prep.setString(1, author);//to find the encrypted password of the HumanUser to constuct it after
                        if (prep.execute()) {//if true then the request has work
                            try (ResultSet tmp = prep.getResultSet()) {//we have retrieved the password of the user
                                tmpUser = HumanUserFactory.createHumanUser(author, tmp.getString("PSWD"));//we can construct our HumanUser
                                tmpMessage = MessageFactory.createMessage(tmpUser, date, message);//we construct a Message
                                msgs.add(tmpMessage);//We add the Message to our List
                            }
                        }
                    }
                }
                hasResult = p2.getMoreResults();//if false we don't have any channel left
            }
            return Collections.unmodifiableList(msgs);//return an unmodifiableList of message to avoid any modification
        }
    }

    @Override
    /*
    *   As always, to retrieve our results, we must works with rwo differents
    * table and ensure that we close ours objects correctly.
    * */
    public List<Channel> getChannelList() {
        ResultSet rs;
        Channel tmpChan;
        try {
            rs = executeQuery("SELECT * FROM CHANNELS;", state);
        } catch (SQLException sql) {
            return new ArrayList<>();//We haven't found any channel on the database, we must return an ArrayList that can be altered later if we added any channel
        }
        List<Channel> channels = new ArrayList<>();//There we know that we will get Channel to add
        try {
            final String request = "SELECT PSWD FROM users WHERE LOGIN LIKE ?;";
            prep = co.prepareStatement(request);
            while (rs.next()) {
                //we retrieve the HumanUser
                String channame = rs.getString("CHANNAME");
                String owner = rs.getString("OWNER");
                prep.setString(1, owner);
                if (prep.execute()) {
                    try (ResultSet tmp = prep.getResultSet()) {
                        while (tmp.next()) {
                            tmpChan = ChannelFactory.createChannel(HumanUserFactory.createHumanUser(owner, tmp.getString(1)), channame);
                            channels.add(tmpChan);
                        }
                    }
                }

            }
            rs.close();
        } catch (SQLException sql) {
            throw new AssertionError("A database error has been occurred");//If any problem occurred during the exploration of our ResultSet
        }
        if (channels.isEmpty()) {
            return new ArrayList<>();
        }
        return channels;
    }

    @Override
    public void close() throws SQLException {
        co.close();
    }


}
