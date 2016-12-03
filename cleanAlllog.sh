#!/bin/bash


echo "Quel est l'emplacement du dossier de log (sans / final)?"
read pth
{ # try
rm $pth/*.log 2> /dev/null
} || {
	echo "Rien a nettoyer"
}
