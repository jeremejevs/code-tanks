package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final class MessageDigestHashFunction extends AbstractStreamingHashFunction
{
  private final String algorithmName;
  private final int bits;

  MessageDigestHashFunction(String paramString)
  {
    this.algorithmName = paramString;
    this.bits = (getMessageDigest(paramString).getDigestLength() * 8);
  }

  public int bits()
  {
    return this.bits;
  }

  private static MessageDigest getMessageDigest(String paramString)
  {
    try
    {
      return MessageDigest.getInstance(paramString);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new AssertionError(localNoSuchAlgorithmException);
    }
  }

  public Hasher newHasher()
  {
    return new MessageDigestHasher(getMessageDigest(this.algorithmName), null);
  }

  private static class MessageDigestHasher
    implements Hasher
  {
    private final MessageDigest digest;
    private final ByteBuffer scratch;
    private boolean done;

    private MessageDigestHasher(MessageDigest paramMessageDigest)
    {
      this.digest = paramMessageDigest;
      this.scratch = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
    }

    public Hasher putByte(byte paramByte)
    {
      checkNotDone();
      this.digest.update(paramByte);
      return this;
    }

    public Hasher putBytes(byte[] paramArrayOfByte)
    {
      checkNotDone();
      this.digest.update(paramArrayOfByte);
      return this;
    }

    public Hasher putBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      checkNotDone();
      Preconditions.checkPositionIndexes(paramInt1, paramInt1 + paramInt2, paramArrayOfByte.length);
      this.digest.update(paramArrayOfByte, paramInt1, paramInt2);
      return this;
    }

    public Hasher putShort(short paramShort)
    {
      checkNotDone();
      this.scratch.putShort(paramShort);
      this.digest.update(this.scratch.array(), 0, 2);
      this.scratch.clear();
      return this;
    }

    public Hasher putInt(int paramInt)
    {
      checkNotDone();
      this.scratch.putInt(paramInt);
      this.digest.update(this.scratch.array(), 0, 4);
      this.scratch.clear();
      return this;
    }

    public Hasher putLong(long paramLong)
    {
      checkNotDone();
      this.scratch.putLong(paramLong);
      this.digest.update(this.scratch.array(), 0, 8);
      this.scratch.clear();
      return this;
    }

    public Hasher putFloat(float paramFloat)
    {
      checkNotDone();
      this.scratch.putFloat(paramFloat);
      this.digest.update(this.scratch.array(), 0, 4);
      this.scratch.clear();
      return this;
    }

    public Hasher putDouble(double paramDouble)
    {
      checkNotDone();
      this.scratch.putDouble(paramDouble);
      this.digest.update(this.scratch.array(), 0, 8);
      this.scratch.clear();
      return this;
    }

    public Hasher putBoolean(boolean paramBoolean)
    {
      return putByte((byte)(paramBoolean ? 1 : 0));
    }

    public Hasher putChar(char paramChar)
    {
      checkNotDone();
      this.scratch.putChar(paramChar);
      this.digest.update(this.scratch.array(), 0, 2);
      this.scratch.clear();
      return this;
    }

    public Hasher putString(CharSequence paramCharSequence)
    {
      for (int i = 0; i < paramCharSequence.length(); i++)
        putChar(paramCharSequence.charAt(i));
      return this;
    }

    public Hasher putString(CharSequence paramCharSequence, Charset paramCharset)
    {
      return putBytes(paramCharSequence.toString().getBytes(paramCharset));
    }

    public Hasher putObject(Object paramObject, Funnel paramFunnel)
    {
      checkNotDone();
      paramFunnel.funnel(paramObject, this);
      return this;
    }

    private void checkNotDone()
    {
      Preconditions.checkState(!this.done, "Cannot use Hasher after calling #hash() on it");
    }

    public HashCode hash()
    {
      this.done = true;
      return HashCodes.fromBytesNoCopy(this.digest.digest());
    }
  }
}

/* Location:           D:\stuff\work\random\CodeTanks\#local-runner\local-runner\
 * Qualified Name:     com.google.common.hash.MessageDigestHashFunction
 * JD-Core Version:    0.6.2
 */