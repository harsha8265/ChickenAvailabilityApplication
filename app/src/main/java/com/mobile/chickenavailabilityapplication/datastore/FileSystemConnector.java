package com.mobile.chickenavailabilityapplication.datastore;

import android.content.Context;

import com.mobile.chickenavailabilityapplication.util.Crypto;
import com.mobile.chickenavailabilityapplication.ChickenAvailabilityApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

public class FileSystemConnector {

    private Context mContext;

    public FileSystemConnector() {
        this.mContext = ChickenAvailabilityApplication.getContext();
    }

    public void writeBytes(byte[] bytesToBeWritten, String fileName) {
        BufferedOutputStream bos = null;
        try {
            FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            bos = new BufferedOutputStream(fos);
            bos.write(bytesToBeWritten);
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeOutputStream(bos);
        }
    }

    public byte[] readBytes(String fileName) {
        byte[] readBytesData = null;
        BufferedInputStream bis = null;
        try {
            InputStream openFileInput = getInputStream(fileName);
            if (openFileInput != null) {
                bis = new BufferedInputStream(openFileInput);
                readBytesData = new byte[bis.available()];
                bis.read(readBytesData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeInputStream(bis);
        }
        return readBytesData;
    }

    public void write(Object data, String fileName, boolean useEncryption, String secretKey)
            throws IllegalAccessException {
        if (useEncryption && secretKey == null) {
            throw new IllegalAccessException("Security key should not be empty, so first call"
                    + "DataObjectStore#readSecretKey(..)");
        }
        if (data == null) {
            System.out.println("Null Data For = " + fileName);
            return;
        }

        ObjectOutputStream oos = null;
        try {
            FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            if (useEncryption) {
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                Cipher cipher = Crypto.createCipher(secretKey.getBytes(), Cipher.ENCRYPT_MODE);
                CipherOutputStream cos = new CipherOutputStream(bos, cipher);
                oos = new ObjectOutputStream(cos);
            } else {
                oos = new ObjectOutputStream(fos);
            }
            oos.writeObject(data);
            oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeOutputStream(oos);
        }
    }

    public Object read(String fileName, boolean useEncryption, String secretKey)
            throws Exception {
        if ((fileName == null) || (useEncryption && (secretKey == null))) {
            throw new IllegalArgumentException("either filename or secret key are null both "
                    + "should be non-null = fileName < " + fileName + "; secretKey < " + secretKey);
        }

        Object object = null;
        ObjectInputStream ois = null;

        try {
            InputStream fis = getInputStream(fileName);
            if (fis != null) {
                if (useEncryption) {
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    Cipher cipher = Crypto.createCipher(secretKey.getBytes(), Cipher.DECRYPT_MODE);
                    CipherInputStream cis = new CipherInputStream(bis, cipher);
                    ois = new ObjectInputStream(cis);
                } else {
                    ois = new ObjectInputStream(fis);
                }
                object = ois.readObject();
            }
        } finally {
            closeInputStream(ois);
        }

        return object;
    }

    public void write(Object dataObject, String secretKey, boolean useEncryption)
            throws IllegalAccessException {
        if (useEncryption && secretKey == null) {
            throw new IllegalAccessException();
        }
        write(dataObject, null, secretKey, useEncryption);
    }

    public void write(final Object dataObject, String fileName, final String secretKey,
                      final boolean useEncryption) throws IllegalAccessException {
        if (useEncryption && secretKey == null) {
            throw new IllegalAccessException("Security key should not be empty");
        }
        if (fileName == null) {
            fileName = dataObject.getClass().getSimpleName();
        }

        write(dataObject, fileName, useEncryption, secretKey);
    }

    public <T extends Object> T read(Class<? extends T> dataObjectClass, String secretKey,
                                     boolean useEncryption) throws Exception {
        return dataObjectClass
                .cast(read(dataObjectClass.getSimpleName(), useEncryption, secretKey));
    }

    public void writeFile(String path, String fileName, byte[] dataBytes) {
        FileOutputStream fileOutputStream = null;
        try {
            File imageDirectory = new File(mContext.getFilesDir() + "/" + path + "/");
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }
            File fileWithInDir = new File(imageDirectory, fileName);
            if (fileWithInDir.exists()) {
                fileWithInDir.delete();
            }
            fileOutputStream = new FileOutputStream(fileWithInDir);
            fileOutputStream.write(dataBytes);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] readFile(String folderPath, String fileName) {
        byte[] readBytesData = null;
        BufferedInputStream bis = null;
        try {
            File imageDirectory = new File(mContext.getFilesDir() + "/" + folderPath + "/");
            if (!imageDirectory.exists()) {
                return null;
            }
            File fileWithInDir = new File(imageDirectory, fileName);
            InputStream openFileInput = new FileInputStream(fileWithInDir);
            if (openFileInput != null) {
                bis = new BufferedInputStream(openFileInput);
                readBytesData = new byte[bis.available()];
                bis.read(readBytesData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeInputStream(bis);
        }
        return readBytesData;
    }

    public String getAbsoluteFilePath(String folderPath, String fileName) {
        File imagesFolder = new File(mContext.getFilesDir() + "/" + folderPath);
        String pathToImagesFolder = imagesFolder.getAbsolutePath();
        return pathToImagesFolder + "/" + fileName;
    }

    public boolean isFileExists(String fileName) {
        boolean result = false;
        File file = mContext.getFileStreamPath(fileName);
        result = file.exists();
        if (!result) {
            try {
                result = Arrays.asList(mContext.getAssets().list("")).contains(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public boolean isFileExists(String folderPath, String fileName) {
        File imageDirectory = new File(mContext.getFilesDir().getAbsolutePath() + "/" + folderPath
                + "/" + fileName);
        return imageDirectory.exists();
    }

    public void deleteFile(String fileName) {
        File file = mContext.getFileStreamPath(fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    public void deleteFolder(String folder) {
        String path = mContext.getFilesDir().getAbsolutePath() + "/"
                + (folder != null && folder.length() > 0 ? folder + "/" : "");
        File directory = new File(path);
        File[] directoryFiles = directory.listFiles();

        if (directoryFiles != null) {
            for (File file : directoryFiles) {
                if (file.exists()) {
                    if (file.isDirectory()) {
                        deleteFolder(folder + "/" + file.getName());
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    private InputStream getInputStream(String fileName) {
        InputStream inputStream = null;
        try {
            inputStream = mContext.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        }
        if (inputStream == null) {
            try {
                inputStream = mContext.getAssets().open(fileName);
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
        return inputStream;
    }

    private void closeInputStream(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeOutputStream(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String GetClassName() {
        return this.getClass().getSimpleName();
    }
}
