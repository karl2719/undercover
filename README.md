compilation 

env -i \
HOME=$HOME \
PATH=/usr/bin:/bin \
DISPLAY=$DISPLAY \
XAUTHORITY=$XAUTHORITY \
/usr/bin/java \
-Dswing.defaultlaf=javax.swing.plaf.nimbus.NimbusLookAndFeel \
-cp bin \
com.black.main.UndercoverApp
