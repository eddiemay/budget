#!/bin/sh
#/usr/local/bin/protoc --proto_path=/Users/eddiemay/Documents/Projects/common/src/conf --proto_path=/usr/local/include --js_out=/Users/eddiemay/Documents/Projects/common/src-gen/js /Users/eddiemay/Documents/Projects/common/src/conf/common_ui.proto
/usr/local/bin/protoc --proto_path=/Users/eddiemay/Documents/Projects/common/src/conf --proto_path=/Users/eddiemay/Documents/Projects/budget/src --proto_path=/usr/local/include --js_out=/Users/eddiemay/Documents/Projects/budget/src-gen/js /Users/eddiemay/Documents/Projects/budget/src/conf/budget_ui.proto
