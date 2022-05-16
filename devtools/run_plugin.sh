#!/bin/bash

set -ueo pipefail

repo_root="$(cd ./"$(git rev-parse --show-cdup)" || exit; pwd)"

java -jar "${repo_root}/app/build/libs/app.jar"

