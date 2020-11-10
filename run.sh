#!/usr/bin/env sh

if [ "$JAVA_MODULE_SYSTEM_ENABLED" == "true" ]; then
  echo "Starting java using MODULE-SYSTEM"
  export JPMS_SWITCHES=""
  exec java $JPMS_SWITCHES -p /app/lib -m no.ssb.dapla.dataset.doc.service/no.ssb.dapla.dataset.doc.service.ExplorationConceptIngestApplication
else
  echo "Starting java using CLASSPATH"
  exec java -cp "/app/lib/*" no.ssb.dapla.dataset.doc.service.DatasetDocApplication
fi
