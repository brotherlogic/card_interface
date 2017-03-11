rm card.jar
rsync -avz --progress 192.168.68.34:codestore/card_ui/ ./codestore
ln -s $(find codestore/ | grep jar-with-dependencies.jar$ | sort -n | tail -n 1) ./card.jar
