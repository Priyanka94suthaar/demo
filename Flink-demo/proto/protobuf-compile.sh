cd $(dirname $0)
SRC_DIR=.
DST_DIR=./dist
protoc -I=$SRC_DIR --java_out=$DST_DIR $SRC_DIR/album.proto