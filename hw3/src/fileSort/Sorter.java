package fileSort;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * This class sorts file containing long values with External Merge sorting rule. <br>
 * {@link Sorter#CHUNK_SIZE} is the maximum amount of lines in a single chunk (partial file).
 * This value is used for splitting the initial file into smaller files, so that each smaller file
 * contains {@link Sorter#CHUNK_SIZE} lines or less.<br>
 * {@link Sorter#BUFFER_SIZE} is the maximum size(lines) of a buffer used for files merge. During merge,
 * {@link Sorter#BUFFER_SIZE} lines or less are loaded from chunks. <br>
 */
public class Sorter {
    private final int CHUNK_SIZE;
    private final int BUFFER_SIZE;

    public Sorter(int chunkSize, int bufferSize) {
        this.CHUNK_SIZE = chunkSize;
        this.BUFFER_SIZE = bufferSize;
    }

    public Sorter() {
        this(13, 3);
    }

    public File sortFile(File dataFile) throws IOException {
        int chunksAmount = splitIntoChunks(dataFile);
        File sortedFile = new File("sortedFile.txt");
        return mergeChunks(sortedFile, chunksAmount);
    }

    /**
     * This method splits the initial file into smaller files. The amount of lines in each smaller file
     * is defined by {@link Sorter#CHUNK_SIZE}. This amount of lines is read from the initial file into list, and then tha
     * {@link Sorter#sortChunkAndWriteToFile} method is called for sorting and saving to a smaller file.
     * The process is repeated until we reach the end of initial file.
     *
     * @param dataFile initial file to be sorted
     * @return total amount of files/chunks created
     * @throws IOException
     * @see Sorter#sortChunkAndWriteToFile
     */
    private int splitIntoChunks(File dataFile) throws IOException {
        int chunksAmount = 0;
        try (Scanner scanner = new Scanner(new FileInputStream(dataFile))) {
            while (scanner.hasNextLong()) {
                int lines = 0;
                ArrayList<Long> nextChunk = new ArrayList<>(CHUNK_SIZE);
                while (lines < CHUNK_SIZE && scanner.hasNextLong()) {
                    Long nextLong = scanner.nextLong();
                    nextChunk.add(nextLong);
                    lines++;
                }
                sortChunkAndWriteToFile(nextChunk, ++chunksAmount);
            }
        }
        return chunksAmount;
    }

    /**
     * This method sorts the list of Longs received and writes the result to a new file
     *
     * @param chunk   list of Longs to be sorted and saved
     * @param chunkId id of each chunk to be used as file name
     * @throws IOException
     */
    private void sortChunkAndWriteToFile(ArrayList<Long> chunk, int chunkId) throws IOException {
        chunk.sort(Long::compareTo);
        File nextFile = new File(chunkId + ".txt");
        nextFile.deleteOnExit();
        try (PrintWriter pw = new PrintWriter(nextFile)) {
            for (Long nextLong : chunk) {
                pw.println(nextLong);
            }
            pw.flush();
        }
    }

    /**
     * This method merges sorted chunk files into one sorted file. In uses k-way merge algorithm.
     * The {@link Sorter#BUFFER_SIZE} amount of lines loaded from each chunk and wrapped into {@link QueueNode} <br>
     * Each {@link QueueNode} element is added to {@link PriorityQueue} based on the smallest value from the chunk.
     * The first (smallest) element of queue is pulled and written to the resulting file.
     * The element is also pulled from the corresponding chunk. Then this updated chunk is added to the queue again.
     * The process is repeated until the queue is empty. <br>
     * Whenever any chunk gets empty, it loads the {@link Sorter#BUFFER_SIZE} amount of lines from corresponding file,
     * until the end of file is reached.
     *
     * @param sortedFile   output file
     * @param chunksAmount the total amount of chunks to be merged (k in k-way merge algorithm)
     * @return sorted file
     * @throws IOException
     */
    private File mergeChunks(File sortedFile, int chunksAmount) throws IOException {
        PriorityQueue<QueueNode> outputQueue = new PriorityQueue<>(Comparator.comparingLong(e -> e.bufferedLongs.peek()));
        List<Scanner> scanners = new ArrayList<>();
        for (int i = 1; i <= chunksAmount; i++) {
            QueueNode nextChunk = new QueueNode(new File(i + ".txt"));
            scanners.add(nextChunk.scanner);
            nextChunk.loadBufferFromFile(BUFFER_SIZE);
            outputQueue.add(nextChunk);
        }
        try (PrintWriter sortedFilePrintWriter = new PrintWriter(sortedFile)) {
            while (!outputQueue.isEmpty()) {
                QueueNode currentNode = outputQueue.poll();
                sortedFilePrintWriter.println(currentNode.poll());
                if (currentNode.isEmpty()) {
                    currentNode.loadBufferFromFile(BUFFER_SIZE);
                }
                if (!currentNode.isEmpty()) {
                    outputQueue.add(currentNode);
                }
            }
            sortedFilePrintWriter.flush();
        } finally {
            scanners.forEach(Scanner::close);
        }
        return sortedFile;
    }

    /**
     * This class is wrapper class for each chunk.
     * {@link QueueNode#bufferedLongs} is list of buffered elements with max {@link Sorter#BUFFER_SIZE} items.
     * {@link QueueNode#scanner} is scanner to read from corresponding file
     */
    private static class QueueNode {
        private final LinkedList<Long> bufferedLongs;
        private final Scanner scanner;

        public QueueNode(File chunkFile) throws IOException {
            this.bufferedLongs = new LinkedList<>();
            this.scanner = new Scanner(new FileInputStream(chunkFile));
        }

        public boolean isEmpty() {
            return bufferedLongs.isEmpty();
        }

        public Long poll() {
            return bufferedLongs.poll();
        }

        public void loadBufferFromFile(int bufferSize) {
            int lines = 0;
            while (scanner.hasNextLong() && lines < bufferSize) {
                bufferedLongs.add(scanner.nextLong());
                lines++;
            }
        }
    }
}
