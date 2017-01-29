package org.jnbt;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

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

public final class NBTReader implements Closeable {
	private final DataInputStream is;
	
	private int nextType = -1;
	private String nextName = null;
	private ArrayList<Integer> depthItems = new ArrayList<Integer>();
	private ArrayList<Integer> depthType = new ArrayList<Integer>();
	private int depth = -1;
	
	@Deprecated
	public NBTReader(InputStream is) throws IOException {
		this(is, NBTCompression.GZIP);
	}
	
	@Deprecated
	public NBTReader(InputStream is, boolean gzipped) throws IOException {
		this(is, gzipped ? NBTCompression.GZIP : NBTCompression.UNCOMPRESSED);
	}
	
	public NBTReader(InputStream is, NBTCompression compression) throws IOException {
		NBTCompression resolvedCompression;
		if (compression == NBTCompression.FROM_BYTE) {
			int compressionByte = is.read();
			if (compressionByte < 0) {
				throw new EOFException();
			}
			resolvedCompression = NBTCompression.fromId(compressionByte);
		} else {
			resolvedCompression = compression;
		}

		switch (resolvedCompression) {
			case UNCOMPRESSED:
				this.is = new DataInputStream(is);
				break;
			case GZIP:
				this.is = new DataInputStream(new GZIPInputStream(is));
				break;
			case ZLIB:
				this.is = new DataInputStream(new InflaterInputStream(is));
				break;
			case FROM_BYTE:
				throw new AssertionError("FROM_BYTE Should have been handled already");
			default:
				throw new AssertionError("[JNBT] Unimplemented " + NBTCompression.class.getSimpleName()
				                         + ": " + compression);
		}
	}
	
	public String nextName() throws IOException {
		nextType();
		if (this.nextName != null) {
			return this.nextName;
		}

		if (this.nextType == NBTConstants.TYPE_END) {
			this.nextName = "";
		} else {
			int    nameLength = is.readShort() & 0xFFFF;
			byte[] nameBytes  = new byte[nameLength];
			is.readFully(nameBytes);
			this.nextName = new String(nameBytes, NBTConstants.CHARSET);
		}
		return this.nextName;
	}
	
	public int nextType() throws IOException {
		if (this.nextType == -1) {
			this.nextType = is.readByte() & 0xFF;
		}
		return this.nextType;
	}
	
	public byte nextByte() throws IOException {
		next();
		return is.readByte();
	}
	
	public short nextShort() throws IOException {
		next();
		return is.readShort();
	}
	
	public int nextInt() throws IOException {
		next();
		return is.readInt();
	}
	
	public long nextLong() throws IOException {
		next();
		return is.readLong();
	}
	
	public float nextFloat() throws IOException {
		next();
		return is.readFloat();
	}
	
	public double nextDouble() throws IOException {
		next();
		return is.readDouble();
	}
	
	public byte[] nextByteArray() throws IOException {
		next();
		int length = is.readInt();
		byte[] data = new byte[length];
		is.readFully(data);
		return data;
	}
	
	public String nextString() throws IOException {
		next();
		int length = is.readShort();
		byte[] bytes = new byte[length];
		is.readFully(bytes);
		return new String(bytes, NBTConstants.CHARSET);
	}
	
	public int[] nextIntArray() throws IOException {
		next();
		int length = is.readInt();
		int[] array = new int[length];
		for (int i = 0; i < length; i++) array[i] = is.readInt();
		return array;
	}
	
	private void next() throws IOException {
		this.nextName = null;
		int itemsLeft = (this.depth < 0 ? -1 : getRemainingItems());
		if (itemsLeft > 0) {
			itemsLeft--;
			this.depthItems.set(this.depth, itemsLeft);
			this.nextType = this.depthType.get(this.depth);
		} else if (itemsLeft == 0) {
			throw new IOException("[JNBT] Attempted to read next element from a list with no remaining elements!");
		} else {
			this.nextType = -1;
		}
	}
	
	private int getRemainingItems() {
		return this.depthItems.get(this.depth);
	}
	
	public void beginObject() {
		this.nextName = null;
		this.nextType = -1;
		this.depthItems.add(-1);
		this.depthType.add(-1);
		this.depth++;
	}
	
	public void beginArray() throws IOException {
		this.nextName = null;
		int type = is.readByte();
		int length = is.readInt();
		this.nextType = type;
		this.depthItems.add(length);
		this.depthType.add(type);
		this.depth++;
	}
	
	public boolean hasNext() throws IOException {
		if (this.depth < 0) {
			throw new IOException("[JNBT] hasNext() cannot be called outside of an object (TAG_Compound) or array (TAG_List)!");
		}
		int itemsLeft = getRemainingItems();
		return itemsLeft > 0 || (itemsLeft == -1 && nextType() != NBTConstants.TYPE_END);
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
		next();
	}
	
	public void endObject() throws IOException {
		int itemsLeft = getRemainingItems();
		if (itemsLeft != -1) {
			throw new IOException("[JNBT] Attempted to end an array using endObject()!");
		}
		if (this.nextType != NBTConstants.TYPE_END) {
			throw new IOException("[JNBT] Attempted to end an object prematurely!");
		}
		this.depth--;
		this.depthItems.remove(this.depthItems.size() - 1);
		this.depthType.remove(this.depthType.size() - 1);
		next();
	}
	
	public void skipValue() throws IOException {
		skipValue(this.nextType);
		next();
	}
	
	private void skipValue(int type) throws IOException {
		int length = 0;
		
		switch (type) {
			case NBTConstants.TYPE_END:
				length = 0;
				break;
				
			case NBTConstants.TYPE_BYTE:
				length = 1;
				break;
				
			case NBTConstants.TYPE_SHORT:
				length = 2;
				break;
				
			case NBTConstants.TYPE_INT:
			case NBTConstants.TYPE_FLOAT:
				length = 4;
				break;
				
			case NBTConstants.TYPE_LONG:
			case NBTConstants.TYPE_DOUBLE:
				length = 8;
				break;
				
			case NBTConstants.TYPE_BYTE_ARRAY:
				length = is.readInt();
				break;
				
			case NBTConstants.TYPE_STRING:
				length = is.readUnsignedShort();
				break;
				
			case NBTConstants.TYPE_LIST:
				int listType = is.readByte();
				int listLength = is.readInt();
				for (int i = 0; i < listLength; i++) {
					skipValue(listType);
				}
				length = 0;
				break;
				
			case NBTConstants.TYPE_COMPOUND:
				int compType = is.readByte() & 0xFF;
				while (compType != NBTConstants.TYPE_END) {
					int nameLength = is.readShort() & 0xFFFF;
					is.skip(nameLength);
					skipValue(compType);
					compType = is.readByte() & 0xFF;
				}
				length = 0;
				break;
				
			case NBTConstants.TYPE_INT_ARRAY:
				length = is.readInt() * 4;
				break;
				
			default:
				throw new IOException("[JNBT] Invalid tag type: " + this.nextType + '.');
		}
		
		if (length > 0) is.skip(length);
	}

	@Override
	public void close() throws IOException {
		is.close();
	}
}
