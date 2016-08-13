/**
 * This class contains utility methods for use in the UI.
 * Also, after seeing that the Music class was very small and contained only static methods, it was merged into this.
 */
package misc;
import gui.TileWorld;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;


public class Util {

	private static Properties props;

	static{
		//initialize placeholder image to use if actual images cannot be found
		BufferedImage input0 = null;
		try{
			input0=ImageIO.read(new File("img"+File.separator+"err.png"));
		}catch(IOException e2){
			TileWorld.filesIncorrect=true;
		}
		err=input0;
		props = new Properties();
		try {
			FileInputStream propsIn = new FileInputStream("warpage.properties");
			props.load(propsIn);
			propsIn.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	///////Image loading///////

	static boolean custom=true;
	public static final BufferedImage err;

	//loads image from custom texture pack folder
	public static BufferedImage loadCustomImg(String name){
		if(!custom)return loadImg(name);
		BufferedImage img;
		try{
			img=ImageIO.read(new File("img_"+props.getProperty("customTextures")+File.separator+name+".png"));
		}catch(IOException e1){
			//System.out.println("No custom texture: "+name);
			img=loadImg(name);
		}
		return img;
	}

	//loads image from default location
	public static BufferedImage loadImg(String name){
		BufferedImage img;
		try{
			img=ImageIO.read(new File("img"+File.separator+name+".png"));
		}catch(IOException e2){
			img=err;
			System.out.println("Missing image: "+name);
		}
		return img;
	}
	
	public static String getProperty(String key){
		return props.getProperty(key);
	}
	/*
	///////RNG stuff///////

	private static long randSeed;

	public static double random(){
		randSeed=((randSeed*25214903917l+11)<<16)>>>16;
		return randSeed/(double)(2<<48);
	//}
	 */
	///////Music and soundtrack methods///////

	private static Sequence[] tracks;
	private static Sequencer seq;
	private static Sequence koncd;//easter egg music track

	public static void beginMusic(int d){
		try{
			tracks=new Sequence[TileWorld.NUM_DIMS];
			for(int i=0;i<tracks.length;i++){
				tracks[i]=MidiSystem.getSequence(new File("mus"+File.separator+"music"+i+".mid"));
			}
			koncd=MidiSystem.getSequence(new File("mus"+File.separator+"koncd.mid"));
			seq = MidiSystem.getSequencer();
			seq.open();
			seq.setSequence(tracks[d]);
			seq.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
			seq.start();
		}catch(FileNotFoundException e1){
			System.out.println("Error: cannot find music file");
		}catch(Exception e2){
			e2.printStackTrace();
		}
	}
	public static void setMusic(boolean isMusic){
		if(isMusic)seq.start();
		else seq.stop();
	}
	public static void shiftMusic(int d){
		seq.stop();
		try {
			seq.setSequence(tracks[d]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		seq.start();
	}

	public static void koncd(){
		seq.stop();
		try {
			seq.setSequence(koncd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		seq.start();
	}
}
