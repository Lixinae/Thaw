package fr.umlv.thaw.main;

import fr.umlv.thaw.bot.Bot;
import fr.umlv.thaw.bot.GitBot;
import fr.umlv.thaw.bot.GitHubBot;
import fr.umlv.thaw.bot.RssBot;

/**
 * Project :Thaw
 * Created by Narex on 08/10/2016.
 */
public class Thaw {
    /*
        TODO
     */
    public static void main(String[] args) {
        Bot testGit = new GitBot();
        Bot testGithub = new GitHubBot();
        Bot testRss = new RssBot();

    }
}
