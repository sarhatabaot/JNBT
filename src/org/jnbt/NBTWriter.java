package org.jnbt;

import static org.jnbt.NBTCompression.FROM_BYTE;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterOutputStream;

//@formatter:off

/*
* JNBT License
*
* Copyright (c) 2010 Graham Edgecombe
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
*     * Redistributions of source code must retain the above copyright notice,
*       this list of conditions and the following disclaimer.
*
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*
*     * Neither the name of the JNBT team nor the names of its
*       contributors may be used to endorse or promote products derived from
*       this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*/

//@formatter:on

public final class NBTWriter implements Closeable, Flushable {
	private final DataOutputStream os;
	private final int flags;
	
	private ArrayList<Integer> depthItems = new ArrayList<Integer>();
	private ArrayList<NBTTagType> depthType = new ArrayList<NBTTagType>();
	private int depth = -1;
	private boolean rootWritten = false;
	
	public static final int FLAG_ALLOW_ROOT_TAG_CHAINING = 0x01;
	public static final int FLAG_ALLOW_NON_COMPOUND_ROOT_TAG = 0x02;
	
	@Deprecated
	public NBTWriter(OutputStream os) throws IOException {
		this(os, NBTCompression.GZIP);
	}
	
	@Deprecated
	public NBTWriter(OutputStream os, boolean gzipped) throws IOException {
		this(os, gzipped ? NBTCompression.GZIP : NBTCompression.UNCOMPRESSED);
	}
	
	public NBTWriter(OutputStream os, NBTCompression compression) throws IOException {
		this(os, compression, 0);
	}
	
	@Deprecated
	public NBTWriter(OutputStream os, NBTCompression compression, int flags) throws IOException {
		this.flags = flags;
		switch (compression) {
			case UNCOMPRESSED:
				this.os = new DataOutputStream(os);
				break;
			case GZIP:
				this.os = new DataOutputStream(new GZIPOutputStream(os));
				break;
			case ZLIB:
				this.os = new DataOutputStream(new InflaterOutputStream(os));
				break;
			case FROM_BYTE:
				throw new IllegalArgumentException(FROM_BYTE.name() + " is only for reading.");
			default:
				throw new AssertionError("[JNBT] Unimplemented " + NBTCompression.class.getSimpleName()
				                         + ": " + compression);
		}
	}
	
	private boolean hasFlag(int flag) {
		return (this.flags & flag) == flag;
	}
	
	private void writeTagHeader(String tagName, NBTTagType type) throws IOException {
		if (depth < 0) {
			if (type != NBTTagType.TAG_COMPOUND && !hasFlag(FLAG_ALLOW_NON_COMPOUND_ROOT_TAG)) {
				throw new IOException("[JNBT] Invalid root tag type: " + type.getMojangName() + ".");
			} else if (rootWritten && !hasFlag(FLAG_ALLOW_ROOT_TAG_CHAINING)) {
				throw new IOException("[JNBT] Only one root tag is permitted per file.");
			}
		} else if (getRemainingItems() != -1 && type != this.depthType.get(this.depth)) {
			throw new IOException("[JNBT] Attempted to write a " + type.getMojangName()
								+ " tag to a " + this.depthType.get(this.depth).getMojangName() + " list!");
		} else if (getRemainingItems() == 0) {
			throw new IOException("[JNBT] Cannot write item to list; list size exceeded!");
		}
		rootWritten = true;
		if (depth < 0 || getRemainingItems() == -1) {
			os.writeByte(type.getTypeByte());
			byte[] tagNameBytes = tagName.getBytes(NBTConstants.CHARSET);
			os.writeShort(tagNameBytes.length);
			os.write(tagNameBytes);
		}
		if (depth >= 0 && getRemainingItems() != -1) {
			this.depthItems.set(this.depth, getRemainingItems() - 1);
		}
	}
	
	public void writeByte(ByteTag b) throws IOException {
		writeByte(b.getName(), b.getValue());
	}
	
	public void writeByte(String tagName, byte b) throws IOException {
		writeTagHeader(tagName, NBTTagType.TAG_BYTE);
		os.writeByte(b);
	}
	
	public void writeShort(ShortTag s) throws IOException {
		writeShort(s.getName(), s.getValue());
	}
	
	public void writeShort(String tagName, short s) throws IOException {
		writeTagHeader(tagName, NBTTagType.TAG_SHORT);
		os.writeShort(s);
	}
	
	public void writeInt(IntTag i) throws IOException {
		writeInt(i.getName(), i.getValue());
	}
	
	public void writeInt(String tagName, int i) throws IOException {
		writeTagHeader(tagName, NBTTagType.TAG_INT);
		os.writeInt(i);
	}
	
	public void writeLong(LongTag l) throws IOException {
		writeLong(l.getName(), l.getValue());
	}
	
	public void writeLong(String tagName, long l) throws IOException {
		writeTagHeader(tagName, NBTTagType.TAG_LONG);
		os.writeLong(l);
	}
	
	public void writeFloat(FloatTag f) throws IOException {
		writeFloat(f.getName(), f.getValue());
	}
	
	public void writeFloat(String tagName, float f) throws IOException {
		writeTagHeader(tagName, NBTTagType.TAG_FLOAT);
		os.writeFloat(f);
	}
	
	public void writeDouble(DoubleTag d) throws IOException {
		writeDouble(d.getName(), d.getValue());
	}
	
	public void writeDouble(String tagName, double d) throws IOException {
		writeTagHeader(tagName, NBTTagType.TAG_DOUBLE);
		os.writeDouble(d);
	}
	
	public void writeByteArray(ByteArrayTag ba) throws IOException {
		writeByteArray(ba.getName(), ba.getValue());
	}
	
	public void writeByteArray(String tagName, byte[] ba) throws IOException {
		writeTagHeader(tagName, NBTTagType.TAG_BYTE_ARRAY);
		os.writeInt(ba.length);
		os.write(ba);
	}
	
	public void writeString(StringTag str) throws IOException {
		writeString(str.getName(), str.getValue());
	}
	
	public void writeString(String tagName, String str) throws IOException {
		writeTagHeader(tagName, NBTTagType.TAG_STRING);
		byte[] strBytes = str.getBytes(NBTConstants.CHARSET);
		os.writeShort(strBytes.length);
		os.write(strBytes);
	}
	
	public void writeIntArray(IntArrayTag ia) throws IOException {
		writeIntArray(ia.getName(), ia.getValue());
	}
	
	public void writeIntArray(String tagName, int[] ia) throws IOException {
		writeTagHeader(tagName, NBTTagType.TAG_INT_ARRAY);
		os.writeInt(ia.length);
		for (int i = 0; i < ia.length; i++) os.writeInt(ia[i]);
	}
	
	private int getRemainingItems() {
		return this.depthItems.get(this.depth);
	}
	
	public void beginObject(String tagName) throws IOException {
		writeTagHeader(tagName, NBTTagType.TAG_COMPOUND);
		this.depthItems.add(-1);
		this.depthType.add(NBTTagType.TAG_END);
		this.depth++;
	}
	
	public void beginArray(String tagName, NBTTagType listType, int length) throws IOException {
		writeTagHeader(tagName, NBTTagType.TAG_LIST);
		os.writeByte(listType.getTypeByte());
		os.writeInt(length);
		this.depthItems.add(length);
		this.depthType.add(listType);
		this.depth++;
	}
	
	public void endArray() throws IOException {
		int itemsLeft = getRemainingItems();
		if (itemsLeft == -1) {
			throw new IOException("[JNBT] Attempted to end an object using endArray()!");
		} else if (itemsLeft > 0) {
			throw new IOException("[JNBT] Attempted to end an array prematurely!");
		}
		this.depth--;
		this.depthItems.remove(this.depthItems.size() - 1);
		this.depthType.remove(this.depthType.size() - 1);
	}
	
	public void endObject() throws IOException {
		if (this.depth < 0) {
			throw new IOException("[JNBT] Attempted to end non-existent object above the root element!");
		}
		int itemsLeft = getRemainingItems();
		if (itemsLeft != -1) {
			throw new IOException("[JNBT] Attempted to end an array using endObject()!");
		}
		os.writeByte(NBTTagType.TAG_END.getTypeByte());
		this.depth--;
		this.depthItems.remove(this.depthItems.size() - 1);
		this.depthType.remove(this.depthType.size() - 1);
	}

	@Override
	public void close() throws IOException {
		os.close();
	}

	@Override
	public void flush() throws IOException {
		os.flush();
	}
}
