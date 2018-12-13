import sys
import os
import gzip
from cStringIO import StringIO

def gzip_compress(raw_data):
    buf = StringIO()
    with gzip.GzipFile(mode='wb', fileobj=buf) as f:
        f.write(raw_data)
    return buf.getvalue()

def start():
    def doDir(dir):
        if not os.path.isdir(dir):
            return
        files = os.listdir(dir)
        for f in files:
            if f == 'index.html':
                continue
            f = os.path.join(dir, f)
            if f[-3:] == '.js' or f[-4:] == '.css' or f[-5:] == '.html':
                doFile(f)
            doDir(f)

    def doFile(f):
        try:    
            with open(f, 'rb') as file:
                content = file.read()
            content = gzip_compress(content)
            with open(f, 'wb') as file:
                file.write(content)
            print 'Gzip succeed: ' + f
        except Exception as e:
            print 'Fail to gzip file [' + f + ']: ' + repr(e)

    if len(sys.argv) < 2:
        print 'No workspace!';
        return
    workspace = sys.argv[1]
    print 'Start at ' + workspace
    doDir(workspace)

if __name__ == '__main__':
    print 'Hi~'
    start()