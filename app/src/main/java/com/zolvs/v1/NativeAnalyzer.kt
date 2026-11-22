package com.zolvs.v1

object NativeAnalyzer {
    init { System.loadLibrary("zolvs_native") }
    external fun inspectFd(fd: Int): String
}
