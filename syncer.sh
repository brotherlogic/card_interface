git fetch -p
git merge origin/master
git gc
rm card.jar
rsync -avz --progress --delete-after -e "ssh -p 23" 192.168.86.26:codestore/card_ui/ ./codestore
ln -s $(find codestore/ | grep jar-with-dependencies.jar$ | sort -n | tail -n 1) ./card.jar
