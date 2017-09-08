rm card.jar
#rsync -avz --progress --delete-after 192.168.86.64:codestore/card_ui/ ./codestore
ln -s $(find codestore/ | grep jar-with-dependencies.jar$ | sort -n | tail -n 1) ./card.jar
