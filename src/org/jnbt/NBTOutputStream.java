package org.jnbt;

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

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * <p>
 * This class writes <strong>NBT</strong>, or <strong>Named Binary Tag</strong>
 * {@code Tag} objects to an underlying {@code OutputStream}.
 * </p>
 * <p>
 * The NBT format was created by Markus Persson, and the specification may be
 * found at <a href="http://www.minecraft.net/docs/NBT.txt">
 * http://www.minecraft.net/docs/NBT.txt</a>.
 * </p>
 *
 * @author Graham Edgecombe, Jocopa3
 */
public final class NBTOutputStream implements Closeable {
	private final DataOutputStream os;

	/**
	 * Creates a new {@code NBTOutputStream}, which will write data to the
	 * specified underlying output stream, GZip-compressed.
	 */
	public NBTOutputStream(OutputStream os) throws IOException {
		this.os = new DataOutputStream(new GZIPOutputStream(os));
	}

	/**
	 * Creates a new {@code NBTOutputStream}, which will write data to the
	 * specified underlying output stream.
	 *
	 * @param gzipped Whether the output stream should be GZip-compressed.
	 */
	public NBTOutputStream(OutputStream os, boolean gzipped) throws IOException {
		if (gzipped)
			this.os = new DataOutputStream(new GZIPOutputStream(os));
		else
			this.os = new DataOutputStream(os);
	}

	public void writeTag(Tag tag) throws IOException {
		int    type      = NBTUtils.getTypeCode(tag.getClass());
		String name      = tag.getName();
		byte[] nameBytes = name.getBytes(NBTConstants.CHARSET);

		os.writeByte(type);
		os.writeShort(nameBytes.length);
		os.write(nameBytes);

		if (type == NBTConstants.TYPE_END) {
			throw new IOException("[JNBT] Named TAG_End not permitted.");
		}

		writeTagPayload(tag);
	}

	private void writeTagPayload(Tag tag) throws IOException {
		int type = NBTUtils.getTypeCode(tag.getClass());
		switch (type) {
			case NBTConstants.TYPE_END:
				writeEndTagPayload((EndTag)tag);
				break;
			case NBTConstants.TYPE_BYTE:
				writeByteTagPayload((ByteTag)tag);
				break;
			case NBTConstants.TYPE_SHORT:
				writeShortTagPayload((ShortTag)tag);
				break;
			case NBTConstants.TYPE_INT:
				writeIntTagPayload((IntTag)tag);
				break;
			case NBTConstants.TYPE_LONG:
				writeLongTagPayload((LongTag)tag);
				break;
			case NBTConstants.TYPE_FLOAT:
				writeFloatTagPayload((FloatTag)tag);
				break;
			case NBTConstants.TYPE_DOUBLE:
				writeDoubleTagPayload((DoubleTag)tag);
				break;
			case NBTConstants.TYPE_BYTE_ARRAY:
				writeByteArrayTagPayload((ByteArrayTag)tag);
				break;
			case NBTConstants.TYPE_STRING:
				writeStringTagPayload((StringTag)tag);
				break;
			case NBTConstants.TYPE_LIST:
				writeListTagPayload((ListTag)tag);
				break;
			case NBTConstants.TYPE_COMPOUND:
				writeCompoundTagPayload((CompoundTag)tag);
				break;
			case NBTConstants.TYPE_INT_ARRAY:
				writeIntArrayTagPayload((IntArrayTag)tag);
				break;
			default:
				throw new IOException("[JNBT] Invalid tag type: " + type + '.');
		}
	}

	/**
	 * Writes a {@code TAG_Byte} tag.
	 */
	private void writeByteTagPayload(ByteTag tag) throws IOException {
		os.writeByte(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Byte_Array} tag.
	 */
	private void writeByteArrayTagPayload(ByteArrayTag tag) throws IOException {
		byte[] bytes = tag.getValue();
		os.writeInt(bytes.length);
		os.write(bytes);
	}

	/**
	 * Writes a {@code TAG_Compound} tag.
	 */
	private void writeCompoundTagPayload(CompoundTag tag) throws IOException {
		for (Tag childTag : tag.getValue().values())
			writeTag(childTag);

		os.writeByte((byte)0); // end tag - better way?
	}

	/**
	 * Writes a {@code TAG_List} tag.
	 */
	private void writeListTagPayload(ListTag tag) throws IOException {
		Class<? extends Tag> clazz = tag.getType();
		List<Tag>            tags  = tag.getValue();
		int                  size  = tags.size();

		os.writeByte(NBTUtils.getTypeCode(clazz));
		os.writeInt(size);
		for (Tag t : tags)
			writeTagPayload(t);
	}

	/**
	 * Writes a {@code TAG_String} tag.
	 */
	private void writeStringTagPayload(StringTag tag) throws IOException {
		byte[] bytes = tag.getValue().getBytes(NBTConstants.CHARSET);
		os.writeShort(bytes.length);
		os.write(bytes);
	}

	/**
	 * Writes a {@code TAG_Double} tag.
	 */
	private void writeDoubleTagPayload(DoubleTag tag) throws IOException {
		os.writeDouble(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Float} tag.
	 */
	private void writeFloatTagPayload(FloatTag tag) throws IOException {
		os.writeFloat(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Long} tag.
	 */
	private void writeLongTagPayload(LongTag tag) throws IOException {
		os.writeLong(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Int} tag.
	 */
	private void writeIntTagPayload(IntTag tag) throws IOException {
		os.writeInt(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Short} tag.
	 */
	private void writeShortTagPayload(ShortTag tag) throws IOException {
		os.writeShort(tag.getValue());
	}

	/**
	 * Writes a {@code TAG_Int_Array} tag.
	 */
	private void writeIntArrayTagPayload(IntArrayTag tag) throws IOException {
		int[] ints = tag.getValue();
		os.writeInt(ints.length);
		for (int i : ints)
			os.writeInt(i);
	}

	/**
	 * Writes a {@code TAG_Empty} tag.
	 */
	private void writeEndTagPayload(EndTag tag) {
	}

	@Override
	public void close() throws IOException {
		os.close();
	}
}
