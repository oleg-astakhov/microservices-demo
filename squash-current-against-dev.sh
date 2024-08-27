#!/bin/bash

debug_trap() {
    echo "Executing command that will squash all commits on current branch that don't exist on dev branch: $BASH_COMMAND"
    echo "Do you want to continue? (1 = yes / 2 = no)"
    select choice in yes no
    do
        case $choice in
            yes)
                eval "$BASH_COMMAND"
                exit 0
                ;;
            no)
                exit 1
                ;;
            *)
                echo "Invalid choice. Please type in '1' (yes) or '2' (no)"
                ;;
        esac
    done
}

# Enable extdebug option to use DEBUG trap
shopt -s extdebug
trap debug_trap DEBUG

git reset --soft "$(git merge-base HEAD dev)"