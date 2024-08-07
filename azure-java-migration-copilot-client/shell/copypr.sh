#!/bin/bash

copypr() {
  # 检查是否提供了PR_NUMBER参数
  if [ -z "$2" ]; then
    echo "Usage: $0 <PR_NUMBER> <TARGET_FOLDER>"
    exit 1
  fi

  # 参数设置
  REPOSITORY_URL="https://github.com/Azure/appcat-rulesets.git"
  PR_NUMBER=$1
  LOCAL_BRANCH_NAME="pr-$PR_NUMBER"
  FOLDER_NAME=$(basename "$REPOSITORY_URL" .git)
  MAIN_BRANCH_NAME="dev"

  rm -rf $PR_NUMBER
  mkdir -p $PR_NUMBER
  cd $PR_NUMBER
  # 克隆仓库
  git clone $REPOSITORY_URL
  cd $FOLDER_NAME

  # 获取PR分支
  git fetch origin pull/$PR_NUMBER/head:$LOCAL_BRANCH_NAME

  # 切换到PR分支
  git checkout $LOCAL_BRANCH_NAME

  # 获取变化文件列表
  git diff --name-only origin/$MAIN_BRANCH_NAME...$LOCAL_BRANCH_NAME > changed_files.txt

  # 创建一个目录来存放变化文件
  mkdir -p pr_files

  # 下载变化文件
  while IFS= read -r file; do
    echo "Downloading $file ..."
    # 创建文件所在的目录
    mkdir -p "pr_files/$(dirname "$file")"
    # 下载文件内容
    git show "HEAD:$file" > "pr_files/$file"
  done < changed_files.txt

  echo "Download complete form PR $PR_NUMBER."

  cp -r pr_files/* ../../$2
  cd ../..
  rm -rf $PR_NUMBER
}

RESULT="mergedresult"

rm -rf $RESULT
mkdir -p $RESULT

copypr 250 $RESULT