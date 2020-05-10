package com.mbt;

import org.graphwalker.core.condition.EdgeCoverage;
import org.graphwalker.core.condition.TimeDuration;
import org.graphwalker.core.condition.VertexCoverage;
import org.graphwalker.core.generator.RandomPath;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.java.annotation.*;
import org.graphwalker.java.test.TestBuilder;
import org.junit.Test;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

 @GraphWalker(value = "random(edge_coverage(100))", start = "e_Open")
public class MusicPlayerTest extends ExecutionContext implements MusicPlayerModel {
    public final static Path MODEL_PATH = Paths.get("com/mbt/MusicPlayer.graphml");
    private Adapter MusicAdapter = new Adapter();
    private Long timeAtPause;
    private Long timeAtStart = 0L;

    @BeforeExecution
    public void setup() {
        System.out.println("Model: setup"); // http://graphwalker.github.io/fixtures/
        MusicAdapter.init();
    }

    @AfterExecution
    public void teardown() {
        System.out.println("Model: teardown"); // http://graphwalker.github.io/fixtures/
        MusicAdapter.reset();
    }

    @Override
    public void v_FileReady(){
        assertEquals("ready", MusicAdapter.getState());
    }

    @Override
    public void v_FilePlaying(){
        assertEquals("playing", MusicAdapter.getState());
    }

    @Override
    public void v_FilePaused(){
        assertEquals("paused", MusicAdapter.getState());
    }

    @Override
    public void e_Open(){
        MusicAdapter.open("C:\\MBT_HW2\\StarWars60.wav");
        assertTrue(MusicAdapter.getFileExist());
    }

    @Override
    public void e_Start(){
        MusicAdapter.start();
        assertEquals(timeAtStart, MusicAdapter.getTimePos());
    }

    @Override
    public void e_Stop(){
        MusicAdapter.stop();
        assertEquals(timeAtStart, MusicAdapter.getTimePos());
    }

    @Override
    public void e_Pause(){
        MusicAdapter.pause();
        timeAtPause = MusicAdapter.getTimePos();
    }

    @Override
    public void e_Resume(){
        MusicAdapter.resume();
        assertEquals(timeAtPause, MusicAdapter.getTimePos());
    }

    @Test
    public void edgeFunctionalTest() {
        new TestBuilder()
                .setModel(MODEL_PATH)
                .setContext(new MusicPlayerTest())
                .setPathGenerator(new RandomPath(new EdgeCoverage(100)))
                .setStart("e_Open")
                .execute();
    }
    @Test
     public void vertexFunctionalTest() {
         new TestBuilder()
                 .setModel(MODEL_PATH)
                 .setContext(new MusicPlayerTest())
                 .setPathGenerator(new RandomPath(new VertexCoverage((100))))
                 .setStart("e_Open")
                 .execute();
     }

     @Test
     public void stabilityTest() {
         new TestBuilder()
                 .setModel(MODEL_PATH)
                 .setContext(new MusicPlayerTest())
                 .setPathGenerator(new RandomPath(new TimeDuration(20, TimeUnit.SECONDS)))
                 .setStart("e_Open")
                 .execute();
     }
}
