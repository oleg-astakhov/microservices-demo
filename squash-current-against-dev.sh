#!/bin/bash

debug_trap () {
    echo "executing $BASH_COMMAND"
    echo "Allow?"
    select choice in yes no
    do
        if [ "$choice" = "yes" ]; then
          eval $BASH_COMMAND
          exit 0
        elif [ "$choice" = "no" ]; then
          exit 1
        fi
    done
}

shopt -s extdebug
trap debug_trap DEBUG
git reset --soft `git merge-base HEAD dev`