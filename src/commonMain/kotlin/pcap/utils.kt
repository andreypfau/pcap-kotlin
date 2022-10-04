package pcap

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.allocArray

internal fun ByteArray.hex(separator: String = "") =
    joinToString(separator) { it.toUByte().toString(16).padStart(2, '0') }

internal fun MemScope.errbuf() = allocArray<ByteVar>(256)
