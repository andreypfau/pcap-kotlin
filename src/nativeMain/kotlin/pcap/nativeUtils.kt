package pcap

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.allocArray

internal fun MemScope.errbuf() = allocArray<ByteVar>(256)
