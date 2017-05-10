#!/bin/sh

PID_FILE=$1

if [ ! -z "$PID_FILE" ]; then
  if [ -f "$PID_FILE" ]; then
    if [ -s "$PID_FILE" ]; then
      echo "Existing PID file found during start."
      if [ -r "$PID_FILE" ]; then
        PID=`cat "$PID_FILE"`
        ps -p $PID >/dev/null 2>&1
        if [ $? -eq 0 ]; then 
          echo "IndexBridge appears to still be running with PID $PID. Aborted."
          exit 1
        else
          echo "Removing/clearing stale PID file."
          rm -f "$PID_FILE" >/dev/null 2>&1
          if [ $? != 0 ]; then
            if [ -w "$PID_FILE" ]; then
              cat /dev/null > "$PID_FILE"
            else
              echo "Unable to remove or clear stale PID file. Aborted."
              exit 1
            fi
          fi
        fi
      else
        echo "Unable to read PID file. Aborted."
        exit 1
      fi
    else
      rm -f "$PID_FILE" >/dev/null 2>&1
      if [ $? != 0 ]; then
        if [ ! -w "$PID_FILE" ]; then
          echo "Unable to remove or write to empty PID file. Aborted."
          exit 1
        fi
      fi
    fi
  fi
fi

