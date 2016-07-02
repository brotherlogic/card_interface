rm card.jar
rsync -avz --progress 10.0.1.17:codestore ./
ln -s $(find codestore/ | grep .jar$ | sort -n | tail -n 1) ./card.jar
