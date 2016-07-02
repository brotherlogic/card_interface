import os

lines_before = len(os.popen('find ./codestore').readlines())
os.popen('./syncer.sh').readlines()
lines_after = len(os.popen('find ./codestore').readlines())

if lines_before != lines_after:
    os.popen('sudo reboot').readlines()
