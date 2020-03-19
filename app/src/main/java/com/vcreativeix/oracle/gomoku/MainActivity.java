package com.vcreativeix.oracle.gomoku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    final static int maxN = 15;
    private Context context;

    private ImageView[][] ivBroad= new ImageView[maxN][maxN];
    private Drawable[] drBroad = new Drawable[4];

    private  Button btnPlay;
    private  TextView tplayer;
    private  TextView gStatus;

    private int[][] played = new int[maxN][maxN]; //def 0:=null(can play); 1:=Played X; 2:=Played O
    private int winner; //def 0:= none; 1:=Player; 2:=Comp
    private boolean goFirst; //return who play first
    private int whoPlaying;
    private int x,y; // x and y aka axis on broad
    private boolean isPlayed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        btnPlay = (Button) findViewById(R.id.btn_newgame);
        tplayer = (TextView) findViewById(R.id.wPlayer);
        gStatus = (TextView) findViewById(R.id.Gstatus);
        loadRes();
        setListener();
        drawGameZone();
    }

    private void setListener() {
        btnPlay.setText(R.string.nwGame);
        tplayer.setText(R.string.info1);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init_Broad();
                play_match();
            }
        });
    }

    private void play_match() {

        Random rad = new Random();
        whoPlaying = rad.nextInt(2)+1;

        if(whoPlaying == 1 ){
            Toast.makeText(context,"Bạn đi trước!",Toast.LENGTH_SHORT).show();
            playerTurn();
        }else{
            Toast.makeText(context,"Bạn đi sau!",Toast.LENGTH_SHORT).show();
            compTurn();
        }
    }

    private void compTurn() {
        tplayer.setText("Máy");
        if(goFirst){
            goFirst=false;
            Random r = new Random();
            x=r.nextInt(maxN)+1;
            y=r.nextInt(maxN)+1;
            make_a_move();
        }else{
            //Thuật toán tìm nước đi tốt nhất

        }
    }

    private void playerTurn() {
        tplayer.setText("Bạn");
        isPlayed=false;
    }

    private void make_a_move() {

        ivBroad[x][y].setImageDrawable(drBroad[whoPlaying]);
        played[x][y]=whoPlaying;

        if (isNoemptyCell()){
            gStatus.setText("Hòa");
            return;
        }else{
            if(chkWinner()){
                if(winner==1){
                    gStatus.setText("Bạn Thắng!");
                }else gStatus.setText("Bạn Thua!");
                return;
            }
        }

        if(whoPlaying==1){
            whoPlaying=3-whoPlaying;
            compTurn();
        }else{
            whoPlaying=3-whoPlaying;
            playerTurn();
        }
    }

    private boolean chkWinner() {
        if(winner!=0) return true;
        chkBroad(x,0,0,1,x,y);
        return false;
    }

    private void chkBroad(int xx, int yy, int x1, int y1, int x2, int y2) {
        if(winner!=0) return;
        final int rg=4;
        int i,j;
        int xBL=x2-rg*x1;
        int yBL=y2-rg*x2;
        int xAB=x2+rg*x1;
        int yAB=y2+rg*y1;
        String s="";
        i=xx;j=yy;
        while (!inside(i,xBL,xAB) || !inside(j,yBL,yAB)){
            i+=x1;j+=y2;
        }
        while(true){
            s=s+String.valueOf(played[i][j]);
            if(s.length()==5){
                defWinner(s);
                s=s.substring(1,5);
            }
            i+=x1;j+=y2;
            if(!isinBroad(i,j)||!inside(i,xBL,xAB) || !inside(j,yBL,yAB)){
                break;
            }
        }
    }

    private boolean isinBroad(int i, int j) {
        //Kiem tra xem i va j co la toa do tren ban co khong
        if(i<0||i>maxN-1||j<0||j>maxN-1) return false;
        return true;
    }

    private void defWinner(String s) {
        switch (s){
            case "11111": winner = 1;break;
            case "00000": winner = 2;break;
            default:break;
        }
    }

    private boolean inside(int i, int xBL, int xAB) {
        return (i-xBL)*(i-xAB)<=0;
    }

    private boolean isNoemptyCell() {
        for(int i=0 ; i<maxN ; i++){
            for(int j=0 ; j<maxN ; j++){
                if(played[i][j]!=0){
                    return true;
                }
            }
        }
        return false;
    }


    private void init_Broad() {
        //Dọn bàn
        goFirst=true;
        winner=0;
        for(int i=0 ; i<maxN ; i++){
            for(int j=0 ; j<maxN ; j++){
                ivBroad[i][j].setImageDrawable(drBroad[0]);
                played[i][j]=0;
            }
        }
    }

    private void loadRes() {
        drBroad[3] = context.getResources().getDrawable(R.drawable.gamezone_bg);
        drBroad[0] = null;
        drBroad[1] = context.getResources().getDrawable(R.drawable.ic_x_player);
        drBroad[2] = context.getResources().getDrawable(R.drawable.o_player);
     }

    private void drawGameZone(){
        int soCell = Math.round(ScreenW()/maxN);
        LinearLayout.LayoutParams iRow = new LinearLayout.LayoutParams(soCell*maxN, soCell );
        LinearLayout.LayoutParams iCell = new LinearLayout.LayoutParams(soCell, soCell );

        LinearLayout Gamezone = (LinearLayout) findViewById(R.id.gameZone);

        for(int i=0 ; i<maxN ; i++){
            LinearLayout ln = new LinearLayout(context);
            for(int j=0 ; j<maxN ; j++){
                ivBroad[i][j] = new ImageView(context);
                ivBroad[i][j].setBackground(drBroad[3]);
                final int xM = i;
                final int yM = j;
                ivBroad[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(played[x][y]==0){
                            if(whoPlaying==1 || !isPlayed){
                                isPlayed=true;
                                x=xM;y=yM;
                                make_a_move();
                            }
                        }
                    }
                });
                ln.addView(ivBroad[i][j],iCell);
            }
            Gamezone.addView(ln,iRow);
        }
    }

    private float ScreenW(){
        Resources res = context.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.widthPixels;
    }
}
