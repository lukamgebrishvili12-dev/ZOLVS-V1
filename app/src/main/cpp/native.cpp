#include <jni.h>
#include <unistd.h>
#include <cstdint>
#include <string>
#include <elf.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_zolvs_v1_NativeAnalyzer_inspectFd(JNIEnv* env, jobject, jint fd) {
    if (fd < 0) {
        return env->NewStringUTF("Invalid file descriptor");
    }

    unsigned char ident[EI_NIDENT] = {};
    const ssize_t n = pread(fd, ident, sizeof(ident), 0);
    if (n != sizeof(ident)) {
        return env->NewStringUTF("File too small or unreadable");
    }

    if (ident[EI_MAG0] != ELFMAG0 || ident[EI_MAG1] != ELFMAG1 ||
        ident[EI_MAG2] != ELFMAG2 || ident[EI_MAG3] != ELFMAG3) {
        return env->NewStringUTF("Not an ELF file");
    }

    if (ident[EI_CLASS] != ELFCLASS64) {
        return env->NewStringUTF("ELF detected, but not 64-bit");
    }

    if (ident[EI_DATA] != ELFDATA2LSB) {
        return env->NewStringUTF("ELF detected, unsupported byte order");
    }

    Elf64_Ehdr hdr{};
    if (pread(fd, &hdr, sizeof(hdr), 0) != sizeof(hdr)) {
        return env->NewStringUTF("Could not read ELF header");
    }

    if (hdr.e_machine == EM_AARCH64) {
        return env->NewStringUTF("Valid ELF64 ARM64/AArch64 library. Header parsed successfully.");
    }
    if (hdr.e_machine == EM_X86_64) {
        return env->NewStringUTF("Valid ELF64 x86_64 library. This build targets ARM64.");
    }
    return env->NewStringUTF("Valid ELF64 file, architecture is not ARM64.");
}
