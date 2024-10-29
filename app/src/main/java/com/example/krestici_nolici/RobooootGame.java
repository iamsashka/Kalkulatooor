package com.example.krestici_nolici;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;
import androidx.appcompat.app.AppCompatActivity;

public class RobooootGame extends AppCompatActivity {

    private Button[][] btns = new Button[3][3];
    private int[][] board = new int[3][3];
    private int
            botWins = 0,
            botPass = 0,
            botDraw = 0;
    private int
            playerWins = 0,
            playerPass = 0,
            playerDraw = 0;
    private boolean who_play = true;

    Button btn_exit;
    TextView text_turn;
    TextView text_stats;
    private SharedPreferences data;
    private SharedPreferences.Editor editor;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_game);

        text_turn = findViewById(R.id.playerTurnTextView);
        btn_exit = findViewById(R.id.back_to);
        text_stats = findViewById(R.id.statisticsTextView);
        data = getSharedPreferences("krestici_noliciPrefs", MODE_PRIVATE);
        editor = data.edit();

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentt = new Intent(RobooootGame.this, MainActivity.class);
                startActivity(intentt);
            }
        });

        loadStats();
        updateStats();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int buttonId = getResources().getIdentifier("button" + i + j, "id", getPackageName());
                btns[i][j] = findViewById(buttonId);
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int row = i;
                final int columnLine = j;
                btns[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (board[row][columnLine] == 0) {
                            if (who_play) {
                                btns[row][columnLine].setText("X");
                                btns[row][columnLine].setTextColor(Color.parseColor("#000000"));
                                board[row][columnLine] = 1;} else {
                                btns[row][columnLine].setText("O");
                                btns[row][columnLine].setTextColor(Color.parseColor("#000080"));
                                board[row][columnLine] = 2;
                            }
                            who_play = !who_play;
                            who_play_text();
                            if (checkWin()) {
                                gameEnd();
                            } else if (checkDraw()) {
                                Toast.makeText(RobooootGame.this, "Ничья!", Toast.LENGTH_SHORT).show();
                                gameEnd();
                            } else {
                                if (!who_play) {
                                    botTurn();
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    private void botTurn() {
        Random random = new Random();
        int row, col;
        do {
            row = random.nextInt(3);
            col = random.nextInt(3);
        } while (board[row][col] != 0);

        board[row][col] = 2;
        btns[row][col].setText("0");
        btns[row][col].setTextColor(Color.parseColor("#0000FF"));
        who_play = true;
        who_play_text();

        if (checkWin()) {
            gameEnd();
        } else if (checkDraw()) {
            Toast.makeText(RobooootGame.this, "Ничья!", Toast.LENGTH_SHORT).show();
            gameEnd();
        }
    }

    private boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != 0 && board[i][0] == board[i][1] && board[i][0] == board[i][2]) {
                return true;
            }
        }
        for (int j = 0; j < 3; j++) {
            if (board[0][j] != 0 && board[0][j] == board[1][j] && board[0][j] == board[2][j]) {
                return true;
            }
        }
        if (board[0][0] != 0 && board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
            return true;
        }
        if (board[0][2] != 0 && board[0][2] == board[1][1] && board[0][2] == board[2][0]) {
            return true;
        }
        return false;
    }

    private boolean checkDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void who_play_text() {
        if (who_play) {
            text_turn.setText("Turn X");
        } else {
            text_turn.setText("Turn 0");
        }
    }

    private void gameEnd() {
        if (who_play) {
            playerPass++;
            if (!who_play) {
                botWins++;
            }
        } else {
            playerWins++;
            if (!who_play) {
                botPass++;
            }
        }
        if (checkDraw()) {
            playerDraw++;
            botDraw++;
        }
        saveStatistics();
        updateStats();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                btns[i][j].setEnabled(false);
            }
        }
    }

    private void saveStatistics() {
        editor.putInt("Кол-во Побед:", playerWins);
        editor.putInt("Кол-во Проигрышей:", playerPass);
        editor.putInt("Ничья: ", playerDraw);
        editor.putInt("Победы бота: ", botWins);
        editor.putInt("Проигрыш бота: ", botPass);
        editor.putInt("Ничья: ", botDraw);
        editor.apply();
    }

    private void loadStats() {
        playerWins = data.getInt("Кол-во Побед:", 0);
        playerPass = data.getInt("Кол-во Проигрышей:", 0);
        playerDraw = data.getInt("Ничья: ", 0);
        botWins = data.getInt("Победы бота: ", 0);
        botPass = data.getInt("Проигрыш бота:", 0);
        botDraw = data.getInt("Ничья: ", 0);
    }

    private void updateStats() {
        text_stats.setText(
                "           Игрок\n\n" +
                        "    Победы     Проигрыши     Ничья\n" +
                        "      " +    playerWins + "                           " + playerPass + "                 " + playerDraw + "\n" +
                        "\n\n" +
                        "                    Робот\n\n" +
                        "    Победы     Проигрыши     Ничья\n" +
                        "      " +    botWins + "                           " + botPass + "                 " + botDraw + "\n"
        );
    }
    public void resetGame(View view) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                btns[i][j].setText("");
                btns[i][j].setEnabled(true);
                board[i][j] = 0;
            }
        }
        who_play = true;
        who_play_text();
    }
}