package sample;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Controller {
    @FXML
    private MediaPlayer mediaplayer;
    private MediaPlayer mediaplayer1;
    @FXML
    private Stage stage;
    private Parent root;
    @FXML
    private ImageView arrow;
    @FXML
    private Label l1;
    @FXML
    private Label gameStatus;
    @FXML
    private Button b2;
    @FXML
    private ImageView dice;
    public ImageView ply1;
    public ImageView ply2;
    @FXML
    private int[][] snakes = {{99,80},{95,75},{92,88},{74,53},{62,19},{64,60},{46,25},{49,11},{16,6}};//A list of all the snakes
    @FXML
    private int[][] ladders = { {2,38},{7,14},{8,31},{15,26},{21,42},{28,84},{36,44},{51,67},{71,91},{78,98},{87,94}};//A list of all the ladders
    @FXML
    private int[][] coordinates={{272, 628}, {336, 626}, {394, 628}, {456, 628}, {518, 628}, {584, 628}, {639, 628}, {698, 632}, {762, 629}, {820, 627}, {823, 570},
            {764, 571}, {704, 572}, {643, 567}, {582, 570}, {521, 567}, {458, 568}, {396, 569}, {337, 570}, {273, 570}, {270, 510}, {338, 508}, {397, 508}, {464, 512},
            {520, 512}, {577, 506}, {641, 510}, {702, 511}, {756, 511}, {820, 508}, {823, 448}, {764, 453}, {695, 453}, {633, 451}, {579, 449}, {523, 449}, {457, 446},
            {398, 449}, {339, 449}, {271, 447}, {276, 390}, {337, 388}, {401, 386}, {458, 387}, {517, 386}, {576, 386}, {643, 390}, {697, 386}, {765, 390}, {822, 390},
            {825, 333}, {760, 329}, {701, 326}, {642, 326}, {583, 328}, {513, 332}, {456, 332}, {397, 332}, {337, 330}, {268, 333}, {270, 267}, {338, 265}, {401, 267},
            {458, 268}, {519, 269}, {578, 270}, {635, 269}, {701, 266}, {763, 266}, {819, 272}, {824, 211}, {759, 211}, {700, 212}, {638, 212}, {572, 212}, {517, 211}
            , {456, 208}, {393, 207}, {330, 209}, {271, 209}, {271, 148}, {334, 144}, {397, 153}, {458, 151}, {522, 150}, {587, 148}, {640, 148}, {699, 150}, {760, 150},
            {824, 149}, {822, 86}, {757, 86}, {703, 88}, {642, 88}, {580, 87}, {513, 87}, {463, 86}, {402, 89}, {340, 90}, {276, 90}};//Coordinate of all the blocks
    private boolean game=false;
    private boolean gameover = false;
    private boolean player1Play = false;
    private boolean player2Play = false;
    private int player1Pos = 0;//To store the position of player1
    private int player2Pos = 0;//To store the position of player2
    private int pla=0;//This will calculate the number of times we have played the game. (Rolled the die)
    @FXML
    private void but1(ActionEvent event) {
        play1();
        this.gameStatus.setText("             GAME STARTED");
        game=true;
//        this.gameStatus.setText(String.valueOf(game));
        TranslateTransition tt=new TranslateTransition();
        tt.setNode(arrow);
        tt.setDuration(Duration.millis(400));
        tt.setCycleCount(TranslateTransition.INDEFINITE);
        tt.setByX(10);
        tt.setAutoReverse(true);
        tt.play();

    }
    @FXML
    private void but2(ActionEvent event) throws IOException {
        play();
        pla++;//When pla is odd player1 moves when pla is even player2 moves.
        Random rand = new Random();
        int result = rand.nextInt(6);
        int num = result+1;//This will be a number somewhere between [1,6].
        /**
         * Any player will have position 0 before moving.
         * We check if it's their turn to move.
         * We check if they got a 6 or not.
         * If the player gets a 6 then it's Playing ability gets unclocked and it can move.
         */
        if(pla%2!=0){
            GameBoard game = new GameBoard(num, 1, l1, dice);
            game.start();//Start the thread game.
            if(!player1Play){
                if(player1Pos == 0 && num == 6){
                    player1Play = true;
                    pla--;
                    return ;
                }
                else{
                    l1.setText("PLAYER 1. YOUR DIE NEEDS TO ROLL 6 TO START THE GAME.");
                    gameStatus.setText("GAME STARTED. PLAYER-1\n CANNOT MOVE YET. NEED\n TO ROLL 6.");
                }
            }
            if(gameover){
                changescene(1);
            }
            if(player1Play){
                movePlayer(1,num);
            }
        }
        if(pla%2==0){
            GameBoard game = new GameBoard(num, 2, l1, dice);
            game.start();//Start the thread game
            if(!player2Play){
                if(player2Pos == 0 && num == 6){
                    player2Play = true;
                    pla--;
                    return ;
                }
                else{
                    l1.setText("PLAYER 2. YOUR DIE NEEDS TO ROLL 6 TO START THE GAME.");
                    gameStatus.setText("GAME STARTED. PLAYER-2\n CANNOT MOVE YET. NEED\n TO ROLL 6.");
                }
            }
            if(gameover){
                changescene(2);
            }
            if(player2Play){
                movePlayer(2,num);
            }
        }
    }//Where it all begins. Roll the die. Change the image. Move the tokens.
    private void movePlayer(int id,int diceNum){
        /**
         * Cases:-
         * He might have landed on a snake
         * He might have landed on a ladder bottom
         * He might be on a normal block
         * He might be on the 100th block
         * He might have gone beyond the 100th block
         */
        int n=0;
        int m=0;
        String str = "NULL";
        //Algorithm to move player 1
        if(id==1){
            int temp = player1Pos+ diceNum;
            if(temp<=100){
                str = "PLAYER 1 MOVES "+diceNum+" STEPS";
                //translate1(ply1,temp);
                m = temp;
                n = temp;
                Platform.runLater(new Status(str,gameStatus));
                Platform.runLater(new movePieces(ply1,n,m,coordinates,1));
            }
            if(temp>100){
                str = "CANNOT GO OVER 100 BRO!!";
                temp = player1Pos;
                Platform.runLater(new Status(str,gameStatus));
            }
            if(temp == 100){
                str = "GAME OVER. PLAYER 1 WINS.\n CONGRATS BRO!!";
                gameover=true;
                n = 100;
                m = 100;
                Platform.runLater(new Status(str,gameStatus));
                Platform.runLater(new movePieces(ply1,n,m,coordinates,1));
            }
            for(int[] item : snakes){
                if(temp == item[0]){
                    str = "PLAYER-1 BITTEN BY A SNAKE\n AT POSITION "+item[0]+" WILL GO TO "+item[1];
                    temp = item[1];
                    m = item[0];
                    n = item[1];
                    Platform.runLater(new Status(str,gameStatus));
                    Platform.runLater(new movePieces(ply1,n,m,coordinates,1));
                }
            }
            for(int[] item : ladders){
                if(temp == item[0]){
                    str = "PLAYER-1 CLIMB A LADDER \nFROM POSITION "+item[0]+" WILL GO TO "+item[1];
                    temp = item[1];
                    m = item[0];
                    n = item[1];
                    Platform.runLater(new Status(str,gameStatus));
                    Platform.runLater(new movePieces(ply1,n,m,coordinates,1));
                }
            }
            player1Pos = temp;
            //At this point we have the value of n where the movement should halt momentarily
            // and m where the movement should stop finally.
        }
        //Algorithm to move player 2
        if(id==2){
            int temp = player2Pos+ diceNum;
            if(temp<=100){
                str = "PLAYER 2 MOVES "+diceNum+" STEPS";
                n = temp;
                m = temp;
                //translate2(ply2,temp);
                Platform.runLater(new Status(str,gameStatus));
                Platform.runLater(new movePieces(ply2,n,m,coordinates,2));
            }
            if(temp>100){
                str = "CANNOT GO OVER 100 BRO!!";
                temp = player2Pos;
                Platform.runLater(new Status(str,gameStatus));
            }
            if(temp == 100){
                str = "GAME OVER. PLAYER 2 WINS.\n CONGRATS BRO!!";
                gameover=true;
                n = 100;
                m = 100;
                Platform.runLater(new Status(str,gameStatus));
                Platform.runLater(new movePieces(ply2,n,m,coordinates,2));
            }
            for(int[] item : snakes){
                if(temp == item[0]){
                    str = "PLAYER-1 BITTEN BY A SNAKE\n AT POSITION "+item[0]+" WILL GO TO "+item[1];
                    temp = item[1];
                    m = item[0];
                    n = item[1];
                    Platform.runLater(new Status(str,gameStatus));
                    Platform.runLater(new movePieces(ply2,n,m,coordinates,2));
                }
            }
            for(int[] item : ladders){
                if(temp == item[0]){
                    str = "PLAYER-2 CLIMB A LADDER \nFROM POSITION "+item[0]+" WILL GO TO "+item[1];
                    temp = item[1];
                    m = item[0];
                    n = item[1];
                    Platform.runLater(new Status(str,gameStatus));
                    Platform.runLater(new movePieces(ply2,n,m,coordinates,2));
                }
            }
            //At this point we have the value of n where the movement should halt momentarily
            // and m where the movement should stop finally.
            player2Pos = temp;
        }
    }
    //m head of the snake or feet of the ladder
    //n is tail of ladder or end of the ladder

    //to change scene
    public void changescene(int num) throws IOException {

        if(num==2){
            root= FXMLLoader.load(getClass().getResource("gameover.fxml"));
//        stage=(Stage) ((Node)event.getSource()).getScene().getWindow();
            stage=(Stage)(b2.getScene().getWindow());
            stage.setScene(new Scene(root,1080,720));
        }
        if(num==1){
            root= FXMLLoader.load(getClass().getResource("gameend.fxml"));
//        stage=(Stage) ((Node)event.getSource()).getScene().getWindow();
            stage=(Stage)(b2.getScene().getWindow());
            stage.setScene(new Scene(root,1080,720));
        }

    }
    //dice
    public void play(){
        String path = Objects.requireNonNull(getClass().getResource("0001188.mp3")).getPath();
//        System.out.println(path);
        Media media = new Media(new File(path).toURI().toString());
        mediaplayer = new MediaPlayer(media);

        mediaplayer.setStartTime(Duration.seconds(0));
        mediaplayer.setStopTime(Duration.seconds(1));
        mediaplayer.setRate(60.0/56.0);
        mediaplayer.setVolume(0.1);
        mediaplayer.play();
    }
    //background music
    public void play1(){

        String path = Objects.requireNonNull(getClass().getResource("music.mp3")).getPath();
//        System.out.println(path);
        Media media = new Media(new File(path).toURI().toString());
        mediaplayer1 = new MediaPlayer(media);
        mediaplayer1.setCycleCount(MediaPlayer.INDEFINITE);
//        mediaplayer.play();
        mediaplayer1.setStartTime(Duration.seconds(0));
        mediaplayer1.setStopTime(Duration.seconds(22));
        mediaplayer1.setRate(60.0/56.0);
        mediaplayer1.setVolume(0.1);
        mediaplayer1.play();
    }
    //unmute
    public void clk2(MouseEvent mouseEvent) {
        play1();
    }
    //mute
    public void clk1(MouseEvent mouseEvent) {
        mediaplayer1.stop();
    }
}
//----------------------------------------------------------------------------------------------------------------------
class GameBoard extends Thread{
    private int num;
    private int pla;
    private Label l1;
    private ImageView dice;
    public GameBoard(int num, int pla,Label l1,ImageView dice){
        this.num = num;
        this.pla = pla;
        this.l1 = l1;
        this.dice = dice;
    }
    @Override
    public void run(){
        Platform.runLater(new l1Setter(String.valueOf(num),l1,pla));
        //This should update the label
        Platform.runLater(new ImageSetter(num,dice));
        //This should update the image
    }
}
class l1Setter implements Runnable{
    private String str;
    private Label l1;
    private int id;
    public l1Setter(String str,Label l1,int id){
        this.str = str;
        this.l1 = l1;
        this.id = id;
    }
    @Override
    public void run(){
        if(id==1){
            l1.setText("PLAYER-1 GOT "+ str);
        }
        if(id==2){
            l1.setText("PLAYER-2 GOT "+ str);
        }
    }
}
class ImageSetter implements Runnable{
    private int num;
    private ImageView dice;
    public ImageSetter(int num,ImageView dice){
        this.num = num;
        this.dice = dice;
    }
    @Override
    public void run() {
        Image img = null;
        if(num==1){
            img=new Image(getClass().getResourceAsStream("no1.png"));
        }
        if(num==2){
            img=new Image(getClass().getResourceAsStream("no2.png"));
        }
        if(num==3){
            img=new Image(getClass().getResourceAsStream("no3.png"));
        }
        if(num==4){
            img=new Image(getClass().getResourceAsStream("no4.png"));
        }
        if(num==5){
            img=new Image(getClass().getResourceAsStream("no5.png"));
        }
        if(num==6){
            img=new Image(getClass().getResourceAsStream("no6.png"));
        }
        dice.setImage(img);
    }
}
class Status implements Runnable{
    private String str;
    private Label gameStatus;
    public Status(String str,Label gameStatus){
        this.str = str;
        this.gameStatus =gameStatus;
    }

    @Override
    public void run() {
        gameStatus.setText(str);
    }
}
class movePieces implements Runnable{
    private ImageView img;
    private int n;
    private int m;
    private int[][] coordinates;
    private int id;
    public movePieces(ImageView img,int n,int m,int[][] coordinates,int id){
        this.img = img;
        this.n = n;
        this.m = m;
        this.coordinates = coordinates;
        this.id = id;
    }
    @Override
    public void run(){
        if(id==1){
            TranslateTransition tt=new TranslateTransition();
            tt.setDuration(Duration.millis(1000));
            tt.setNode(img);
            tt.setToX(coordinates[n-1][0]-915);
            tt.setToY(coordinates[n-1][1]-155);
            tt.play();
        }
        if(id==2){
            TranslateTransition tt=new TranslateTransition();
            tt.setDuration(Duration.millis(1000));
            tt.setNode(img);
            tt.setToX(coordinates[n-1][0]-1005);
            tt.setToY(coordinates[n-1][1]-150);
            tt.play();
        }
    }
}


/**
 * SNAKES:
 * 99,80
 * 95,75
 * 92,88
 * 74,53
 * 62,19
 * 64,60
 * 46,25
 * 49,11
 * 16,6
 * LADDERS:
 * 2,38
 * 7,14
 * 8,31
 * 15,26
 * 21,42
 * 36,44
 * 51,67
 * 71,91
 * 78,98
 * 87,94
 */