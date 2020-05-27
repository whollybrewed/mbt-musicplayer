# Modeling and Model-based Testing with GraphWalker
Written by Ander Lee
## Part 1
The SUT in this report refers to the [Java Sound Technology](https://docs.oracle.com/javase/8/docs/technotes/guides/sound/index.html) API. This API mainly supports features of interaction with audio file, such as audio playback, audio capturing, audio processing and more. The emphasis of this model will be on the playback related functionalities, specifically the "Open", "Start", "Stop", "Pause", and "Resume" features of a single music file in WAV format. Table 1 shows the system inputs and their descriptions.

Input | Description 
------------ | ------------- 
Open | Open a .wav file from a specific file path
Start | Start audio playback from the beginning 
Stop | Terminate audio playback, reset time position to zero
Pause | Save current time position and pause the audio playback 
Resume | Play the audio from the time position saved before pausing

The state space of the SUT consists of two sets of parameters, namely "Action", and "TimePos". Together These two set form a "State" set in a format State = { Action, TimePos }. The description of individual set is shown as below.

* Action = { Ready, Playing, Paused }
* TimPos = { t } where t is an integer lying within the range of [*0, audio length*] in microsecond.

As the result, a transition table that specifies the system inputs and states relationship is described as the following Table 2.

Before      | Input      | After
----------- | ---------- | ----------
null, 0     | Open       | Ready, 0
Ready, 0    | Start      | Playing, 0
Playing, t  | Stop       | Ready, 0
Pause, t    | Stop       | Ready, 0
Playing, t  | Pause      | Pause, t
Pause, t    | Resume     | Playing, t

A graphical representation of the model is shown as Fig 1 below:

![MusicPlayerVisualModel](https://raw.githubusercontent.com/whollybrewed/mbt-musicplayer/master/MusicPlayerVisualModel.bmp)

*__Figure 1.__ Visualization of the model*

## Part 2

### Stop Criteria
Two stop criteria have been used during the testing, namely ```EdgeCoverage(100)``` and ```VertexCoverage(100)```. Under random traversal, the 100% vertex coverage corresponds to full state coverage of the model, with the runtime of *2.151s*. Note that vertex coverage does not guarantee a full transition coverage of the model, as there are more than one transition that could lead to the same state. While a 100% edge coverage will ensure both states and transitions are fully covered, yet it requires runtime of *5.757s*, more than twice the time needed for vertex coverage.  

From the user standpoint of this SUT, a full vertex coverage means the audio exists and is playable, and a full edge coverage further implies that all playback functionalities are working properly.

### Generators
Random generator and weighted random generator are both used to navigate paths in the model. To mimic the real-life usage behavior when listening to a music, the edge "e_Stop" (or system input "Stop") is given a lower likelihood (30%) for selection. This design assumes a user will tend to listen music in full length, regardless with or without temporary pausing. The weighted random results in approximate the same runtime for full vertex coverage as the uniform random. This is expected since there are alternative paths that could lead to the state which "e_Stop" would have. For full edge coverage, the time taken by weighted random is considerably longer (*8.118s*) comparing with uniform random.

### Test Cases
* Test to ensure all vertices have the correct "Action" arguments in their states.
```java
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
``` 
* Test the existence of the audio file when executing input "Open". Incorrect or non-existing file path will lead to failure. 
```java
@Override
    public void e_Open(){
        MusicAdapter.open("C:\\MBT_HW2\\StarWars60.wav");
        assertTrue(MusicAdapter.getFileExist());
    }
```
* Test whether the audio starts correctly at the beginning when executing input "Start". Failure occurs if the audio format is unsupported or the header is corrupted. 
```java
@Override
public void e_Start(){
    MusicAdapter.start();
    assertEquals(timeAtStart, MusicAdapter.getTimePos());
}
```
* Test whether the audio terminates and resets time position successfully when executing input "Stop".
```java
@Override
public void e_Stop(){
    MusicAdapter.stop();
    assertEquals(timeAtStart, MusicAdapter.getTimePos());
}
```
* Test the procedure which the audio playback is paused by input "Pause" and then resumed by input "Resume", it is expected to play from the correct time position. System interrupt may lead to failure if it causes a time delay between "pause time" and "resume time". 
```java
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
```
* Open music file and interact with playback features for 20 seconds
```java
@Test
public void stabilityTest() {
    new TestBuilder()
            .setModel(MODEL_PATH)
            .setContext(new MusicPlayerTest())
            .setPathGenerator(new RandomPath(new TimeDuration(20, TimeUnit.SECONDS)))
            .setStart("e_Open")
            .execute();
}
```

## Part 3 
Multimedia application such as the SUT in this report normally includes a variety of functionalities in order to provide a better user experience. Model-based testing (MBT) avoids the tedious process of manually interact with the application over and over, by allowing a swift walk through of all features. However, this characteristic of MBT also comes with ignoring of the qualitative components of SUT, which is especially important for multimedia application. 

Take this SUT as an example, verification of subjective qualities such as volume, noise, and stutter are difficult to included in the test model, as they would require a more detailed testing at the hardware I/O side, and possibly need a human user to make subjective evaluation. Both of this additional requirements are not easily integrated into the model. Therefore to some extent, MBT is possibly more suited for the testing of numerical data than said UX or UI related application.

The fast execution of automated testing could also cause a problem when trying to evaluate qualitative 
features, as this kind of near-instant execution does not match with the behavior of real-life usage. Fortunately this issue could be overcame by introducing some delay mechanism in the SUT, for instance ```Thread.sleep()```, to prevent next input to happen to soon. In general, there is a tradeoff between runtime efficiency and how much effects (visual, sounds...etc) the tester is able to observe.


 
