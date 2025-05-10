package org.example.library.bus;

import org.example.library.daos.ReaderDao;
import org.example.library.models.Reader;

import java.util.List;
import java.util.UUID;

public class ReaderBus {
    private final ReaderDao readerDao;

    public ReaderBus() {
        this.readerDao = new ReaderDao();
    }

    public List<Reader> getAllReaders() {
        return readerDao.getReaders();
    }

    public String generateReaderId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public void saveInfo(Reader reader) {
        Reader existingReader = readerDao.findById(reader.getReaderId());

        if (existingReader == null) {
            if (readerDao.isPhoneNumberExists(reader.getPhoneNumber())) {
                throw new IllegalArgumentException("Số điện thoại đã tồn tại!");
            }
        } else {
            if (!existingReader.getPhoneNumber().equals(reader.getPhoneNumber()) &&
                    readerDao.isPhoneNumberExists(reader.getPhoneNumber())) {
                throw new IllegalArgumentException("Số điện thoại đã tồn tại!");
            }
        }

        readerDao.save(reader);
    }

    public void deleteReader(String readerId) {
        readerDao.delete(readerId);
    }

    public List<Reader> search(String keyword) {
        return getAllReaders().stream()
                .filter(reader -> reader.getReaderName().toLowerCase().contains(keyword.toLowerCase())
                        || reader.getReaderId().toLowerCase().contains(keyword.toLowerCase())
                        || reader.getReaderAddress().toLowerCase().contains(keyword.toLowerCase())
                        || reader.getPhoneNumber().toLowerCase().contains(keyword.toLowerCase())
                        || reader.getDob().toLowerCase().contains(keyword.toLowerCase())
                )
                .toList();

    }

    public List<String> getReaderIds() {
        return getAllReaders().stream().map(Reader::getReaderId).toList();
    }

    public Reader findReaderBy(String id) {
        return readerDao.findById(id);
    }
}
