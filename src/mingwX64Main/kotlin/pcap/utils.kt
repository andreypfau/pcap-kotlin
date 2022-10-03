package pcap

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix._pclose
import platform.posix._popen
import platform.posix.fgets

internal fun cmd(command: String) = memScoped {
    val file = _popen(command, "rt")
    val bufLength = 128
    val buf = allocArray<ByteVar>(bufLength)
    val stringBuilder = StringBuilder()
    while (true) {
        val str = fgets(buf, bufLength, file)?.toKString() ?: break
        stringBuilder.append(str)
    }
    _pclose(file)
    stringBuilder.toString()
}
