package net.sf.tweety.arg.delp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kohsuke.args4j.CmdLineException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Testing the command line stuff for DeLP.
 *
 * @author Linda.Briesemeister
 */
public final class TestDeLP {

    // turn text files from resources into paths...
    private static Path PATH_BIRDS;
    private static Path PATH_HOBBES;
    static {
        try {
            PATH_BIRDS = java.nio.file.Paths.get(TestDeLP.class.getResource("/birds.txt").toURI());
            PATH_HOBBES = java.nio.file.Paths.get(TestDeLP.class.getResource("/hobbes.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test(expected = CmdLineException.class)
    public void noArgs() throws IOException, CmdLineException {
        DefeasibleLogicProgram.main(new String[0]);
    }

    @Test
    public void displayHelp() throws IOException, CmdLineException {
        DefeasibleLogicProgram.main(new String[] {"-h"});
        System.out.println();
        DefeasibleLogicProgram.main(new String[] {"--help"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownComp1() throws IOException, CmdLineException {
        DefeasibleLogicProgram.main(new String[] {"-c", "PRIORITY"});
    }
    @Test(expected = IllegalArgumentException.class)
    public void unknownComp2() throws IOException, CmdLineException {
        DefeasibleLogicProgram.main(new String[] {"-c", "FOO"});
    }

    @Test(expected = CmdLineException.class)
    public void noFiles() throws IOException, CmdLineException {
        DefeasibleLogicProgram.main(new String[] {"-q", "FOO"});
    }
    @Test(expected = CmdLineException.class)
    public void cannotRead() throws IOException, CmdLineException {
        DefeasibleLogicProgram.main(new String[] {"-q", "FOO", "BLA"});
    }

    @Test
    public void emptyCrit() throws IOException, CmdLineException {
        DefeasibleLogicProgram.main(new String[] {"-q", "~ Flies(tweety)", "-c", "EMPTY", PATH_BIRDS.toString()});
    }

    @Test
    public void multiFile() throws IOException, CmdLineException {
        DefeasibleLogicProgram.main(new String[] {"-q", "~dangerous(hobbes)", "--verbose",
                PATH_BIRDS.toString(), PATH_HOBBES.toString()});
    }

    @Test(expected = CmdLineException.class)
    public void noQuery() throws IOException, CmdLineException {
        DefeasibleLogicProgram.main(new String[] {PATH_BIRDS.toString()});
    }
    @Test(expected = CmdLineException.class)
    public void emptyQuery() throws IOException, CmdLineException {
        DefeasibleLogicProgram.main(new String[] {"-q", " ", PATH_BIRDS.toString()});
    }
    @Test(expected = CmdLineException.class)
    public void noBatch() throws IOException, CmdLineException {
        DefeasibleLogicProgram.main(new String[]{"-b", "FOO", PATH_BIRDS.toString()});
    }

    private File createBatchFile(List<String> queries) throws IOException {
        File tempFile = tempFolder.newFile();
        Files.write(tempFile.toPath(), queries);
        return tempFile;
    }

    @Test(expected = CmdLineException.class)
    public void emptyBatch() throws IOException, CmdLineException {
        File tempFile = createBatchFile(Arrays.asList("", "  ", "\t"));
        DefeasibleLogicProgram.main(new String[] {"-b", tempFile.getAbsolutePath(), PATH_BIRDS.toString()});
    }

    @Test
    public void queryBatch() throws IOException, CmdLineException {
        File tempFile = createBatchFile(Arrays.asList(" ", " Flies(tweety)  ", " \t ", " Flies(tina)"));
        DefeasibleLogicProgram.main(new String[] {"-b", tempFile.getAbsolutePath(), PATH_BIRDS.toString()});
    }
}
