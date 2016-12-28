#!/bin/bash

brew update
brew install gcc
brew upgrade gcc
brew install cmake
brew upgrade cmake
brew install ant
brew upgrade ant
# TODO revert this once opencv-python has builds for Python 3.6
brew tap drolando/homebrew-deadsnakes
brew install python34
brew test python34
pip3.4 install numpy
pip3.4 install opencv-python
mkdir -p $HOME/opencv/jni
