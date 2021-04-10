set -e

. .env.sh
if [ -z "$JRE_ZIP_DIR" ] ; then
    (>&2 echo "Aborting: JRE_ZIP_DIR not set")
    exit 1
fi

CNT=`ls -1 "$JRE_ZIP_DIR"/*jre11*win*.zip| wc -l`
if [ $CNT != 1 ] ; then
    (>&2 echo "Aborting: Expected one JRE, but found $CNT")
    exit 1
fi
JRE=`ls -1 "$JRE_ZIP_DIR"/zulu*jre11*win*.zip`
echo "Will use $JRE"

gradle clean distZip
cd build/distributions

CNT=`ls -1 AbakusFx*.zip | grep -v '\-src' | wc -l`
if [ $CNT != 1 ] ; then
    cd - > /dev/null
    (>&2 echo "Aborting: Expected one Abakus version, but found $CNT")
    exit 1
fi
ABAZIP=`ls -1 AbakusFx*.zip | grep -v '\-src'`
echo "Bundling $ABAZIP ..."
ABADIR=${ABAZIP%.zip}
rm -rf "$ABADIR"

unzip -q $ABAZIP
mv "${ABADIR}-src.zip" "$ABADIR"
cd ${ABADIR}
unzip -q $JRE

cd ..
zip -qr "$ABADIR.zip" "$ABADIR"
ls -al
