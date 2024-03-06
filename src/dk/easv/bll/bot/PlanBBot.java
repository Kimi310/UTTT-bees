package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static dk.easv.bll.field.IField.AVAILABLE_FIELD;

public class PlanBBot implements IBot{
    private int depth = 5;
    private int botId;
    private int opponentId;
    @Override
    public IMove doMove(IGameState state) {

        if (state.getMoveNumber()==0){
            botId =0;
            opponentId=1;
            return new Move(4,4);
        } else if (state.getMoveNumber()==1) {
            botId=1;
            opponentId=0;
        }
        return evaluatePosition(0,false,state);
    }

    @Override
    public String getBotName() {
        return "PlanBBot";
    }

    private IMove evaluatePosition(int currentDepth, boolean isMaximizer,IGameState state){
        int bestVal = -1000;
        Move bestMove = new Move(0,0);
        String[][] board = state.getField().getBoard();
        for (int i=0;i<3;i++) {
            for (int j = 0; j < 3; j++) {
                for (int k=0;k<findMicroBoard(state.getField().getMacroboard()).length;k++){
                    int x = findMicroBoard(state.getField().getMacroboard())[k][0] * 3 + i;
                    int y = findMicroBoard(state.getField().getMacroboard())[k][1] * 3 + j;
                    if (Objects.equals(state.getField().getBoard()[x][y], ".")) {
                        board[x][y] = String.valueOf(botId);
                        String[][] placeholder = state.getField().getMacroboard();
                        String[][] boards=checkForBigBoardChange(takeMicroboard(board,x,y),x,y,state.getField().getMacroboard());
                        for(int f=0;f<3;f++){
                            for (int g=0;g<3;g++){
                                if (Objects.equals(boards[f][g], "-1")){
                                    boards[f][g]=".";
                                }
                            }
                        }
                        if (Objects.equals(boards[i][j], ".")){
                            boards[i][j] = AVAILABLE_FIELD;
                        } else  {
                            for(int f=0;f<3;f++){
                                for (int g=0;g<3;g++){
                                    if (Objects.equals(boards[f][g], ".")){
                                        boards[f][g]=AVAILABLE_FIELD;
                                    }
                                }
                            }
                        }

                        int moveval = minimax(board, isMaximizer, currentDepth + 1, boards);
                        System.out.println("Evaluation: " + moveval + " for move " + x + " " + y);
                        board[x][y] = ".";
                        if (moveval > bestVal) {
                            bestVal = moveval;
                            bestMove = new Move(x, y);
                        }
                    }
                }
            }
        }
        return bestMove;
    }

    private int[][] findMicroBoard(String[][] boards){
        ArrayList<Integer> availableBoards = new ArrayList<>();
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                if (Objects.equals(boards[i][j], AVAILABLE_FIELD)){
                    availableBoards.add(i);
                    availableBoards.add(j);
                }
            }
        }
        int[][] board = new int[availableBoards.size()/2][2];
        int k=0;
        for (int i=0;i<availableBoards.size()/2;i+=2) {
            board[k][0]=availableBoards.get(i);
            board[k][1]=availableBoards.get(i+1);
            k++;
        }
        return board;
    }

    private int minimax(String[][] board,boolean isMax,int currentDepth, String[][]boards){
        if (currentDepth == depth || evaluateBigBoard(boards)==1000 || evaluateBigBoard(boards)==-1000){
            int positionValue=0;
            for (int i=0;i<3;i++){
                for (int j=0;j<3;j++){
                    String[][] microBoard = takeMicroboard(board,i,j);
                    positionValue +=evalueateMicroBoard(microBoard);
                }
            }
            positionValue+=evaluateBigBoard(boards);
            return positionValue;
        }

        if (isMax){
            int best = -1000;
            for (int i = 0;i<3;i++){
                for (int j=0;j<3;j++){
                    for (int k=0;k<findMicroBoard(boards).length;k++){
                        int x = findMicroBoard(boards)[k][0] * 3 + i;
                        int y = findMicroBoard(boards)[k][1] * 3 + j;
                        if (Objects.equals(board[x][y], ".")){
                            board[x][y]=String.valueOf(botId);
                            String[][] placeholder = boards;
                            boards=checkForBigBoardChange(takeMicroboard(board,x,y),x,y,boards);
                            for(int f=0;f<3;f++){
                                for (int g=0;g<3;g++){
                                    if (Objects.equals(boards[f][g], "-1")){
                                        boards[f][g]=".";
                                    }
                                }
                            }
                            if (Objects.equals(boards[i][j], ".")){
                                boards[i][j] = AVAILABLE_FIELD;
                            } else  {
                                for(int f=0;f<3;f++){
                                    for (int g=0;g<3;g++){
                                        if (Objects.equals(boards[f][g], ".")){
                                            boards[f][g]=AVAILABLE_FIELD;
                                        }
                                    }
                                }
                            }
                            best = Math.max(best,minimax(board, false,currentDepth+1,boards));
                            board[x][y]=".";
                            boards=placeholder;
                        }
                    }
                }
            }
            return best;
        }else {
            int best = 1000;
            for (int i = 0;i<3;i++){
                for (int j=0;j<3;j++){
                    for (int k=0;k<findMicroBoard(boards).length;k++){
                        int x = findMicroBoard(boards)[k][0] * 3 + i;
                        int y = findMicroBoard(boards)[k][1] * 3 + j;
                        if (Objects.equals(board[x][y], ".")){
                            board[x][y]=String.valueOf(opponentId);
                            String[][] placeholder = boards;
                            boards=checkForBigBoardChange(takeMicroboard(board,x,y),x,y,boards);
                            for(int f=0;f<3;f++){
                                for (int g=0;g<3;g++){
                                    if (Objects.equals(boards[f][g], "-1")){
                                        boards[f][g]=".";
                                    }
                                }
                            }
                            if (Objects.equals(boards[i][j], ".")){
                                boards[i][j] = AVAILABLE_FIELD;
                            } else  {
                                for(int f=0;f<3;f++){
                                    for (int g=0;g<3;g++){
                                        if (Objects.equals(boards[f][g], ".")){
                                            boards[f][g]=AVAILABLE_FIELD;
                                        }
                                    }
                                }
                            }
                            best = Math.min(best,minimax(board, true,currentDepth+1,boards));
                            board[x][y]=".";
                            boards=placeholder;
                        }
                    }
                }
            }
            return best;
        }
    }

    private String[][] takeMicroboard(String[][] board, int x,int y){
        String[][] boardState = new String[3][3];
        for (int k = 0;k<3;k++){
            for (int l = 0; l<3; l++){
                boardState[k][l] = board[x*3+k][y*3+l];
            }
        }
        return boardState;
    }

    private int evalueateMicroBoard(String [][] microBoard){
        String[] checkTable = new String[3];
        int evaluation =0;
        int sum =0;
        for (int i=0;i<3;i++){ // check cols
            for(int j=0;j<3;j++){
                checkTable[j] = microBoard[i][j];
            }
            evaluation = checkCheckTable(checkTable);
            if (evaluation == 100 || evaluation==-100){
                return evaluation;
            }
            sum+=evaluation;
        }
        for (int i=0;i<3;i++){ // check rows
            for(int j=0;j<3;j++){
                checkTable[j] = microBoard[j][i];
            }
            evaluation = checkCheckTable(checkTable);
            if (evaluation == 100 || evaluation==-100){
                return evaluation;
            }
            sum+=evaluation;
        }
        for (int i = 0; i < 3; i++) { // check diagonal one
            checkTable[i] = microBoard[i][i];
        }
        evaluation = checkCheckTable(checkTable);
        if (evaluation == 100 || evaluation==-100){
            return evaluation;
        }
        sum+=evaluation;

        for (int i = 2; i > -1; i--) { // check diagonal two
            checkTable[i] = microBoard[i][i];
        }
        evaluation = checkCheckTable(checkTable);
        if (evaluation == 100 || evaluation==-100){
            return evaluation;
        }
        sum+=evaluation;
        return sum;
    }

    private int evaluateBigBoard(String[][] bigTable){
        String[] checkTable = new String[3];
        int evaluation =0;
        int sum =0;
        for (int i=0;i<3;i++){ // check cols
            for(int j=0;j<3;j++){
                checkTable[j] = bigTable[i][j];
            }
            evaluation = checkBigTable(checkTable);
            if (evaluation == 1000 || evaluation==-1000){
                return evaluation;
            }
            sum+=evaluation;
        }
        for (int i=0;i<3;i++){ // check rows
            for(int j=0;j<3;j++){
                checkTable[j] = bigTable[j][i];
            }
            evaluation = checkBigTable(checkTable);
            if (evaluation == 1000 || evaluation==-1000){
                return evaluation;
            }
            sum+=evaluation;
        }
        for (int i = 0; i < 3; i++) { // check diagonal one
            checkTable[i] = bigTable[i][i];
        }
        evaluation = checkBigTable(checkTable);
        if (evaluation == 1000 || evaluation==-1000){
            return evaluation;
        }
        sum+=evaluation;

        for (int i = 2; i > -1; i--) { // check diagonal two
            checkTable[i] = bigTable[i][i];
        }
        evaluation = checkBigTable(checkTable);
        if (evaluation == 1000 || evaluation==-1000){
            return evaluation;
        }
        sum+=evaluation;
        return sum;
    }

    private int checkCheckTable(String[] checkTable){
        if (Objects.equals(checkTable[0], String.valueOf(botId)) && Objects.equals(checkTable[1], String.valueOf(botId)) && Objects.equals(checkTable[2], String.valueOf(botId))) {
            return 100;
        }
        if (Objects.equals(checkTable[0], String.valueOf(opponentId)) && Objects.equals(checkTable[1], String.valueOf(opponentId)) && Objects.equals(checkTable[2], String.valueOf(opponentId))) {
            return -100;
        }
        if (Objects.equals(checkTable[0], String.valueOf(botId)) && Objects.equals(checkTable[1], String.valueOf(botId)) && !Objects.equals(checkTable[2], String.valueOf(opponentId))){
            return 4;
        }
        if (!Objects.equals(checkTable[0], String.valueOf(opponentId)) && Objects.equals(checkTable[1], String.valueOf(botId)) && !Objects.equals(checkTable[2], String.valueOf(botId))){
            return 4;
        }
        if (Objects.equals(checkTable[0], String.valueOf(opponentId)) && Objects.equals(checkTable[1], String.valueOf(opponentId)) && !Objects.equals(checkTable[2], String.valueOf(botId))){
            return -4;
        }
        if (!Objects.equals(checkTable[0], String.valueOf(botId)) && Objects.equals(checkTable[1], String.valueOf(opponentId)) && !Objects.equals(checkTable[2], String.valueOf(opponentId))){
            return -4;
        }
        return 0;
    }
    private int checkBigTable(String[] checkTable){
        if (Objects.equals(checkTable[0], String.valueOf(botId)) && Objects.equals(checkTable[1], String.valueOf(botId)) && Objects.equals(checkTable[2], String.valueOf(botId))) {
            return 1000;
        }
        if (Objects.equals(checkTable[0], String.valueOf(opponentId)) && Objects.equals(checkTable[1], String.valueOf(opponentId)) && Objects.equals(checkTable[2], String.valueOf(opponentId))) {
            return -1000;
        }
        if (Objects.equals(checkTable[0], String.valueOf(botId)) && Objects.equals(checkTable[1], String.valueOf(botId)) && !Objects.equals(checkTable[2], String.valueOf(opponentId))){
            return 40;
        }
        if (!Objects.equals(checkTable[0], String.valueOf(opponentId)) && Objects.equals(checkTable[1], String.valueOf(botId)) && !Objects.equals(checkTable[2], String.valueOf(botId))){
            return 40;
        }
        if (Objects.equals(checkTable[0], String.valueOf(opponentId)) && Objects.equals(checkTable[1], String.valueOf(opponentId)) && !Objects.equals(checkTable[2], String.valueOf(botId))){
            return -40;
        }
        if (!Objects.equals(checkTable[0], String.valueOf(botId)) && Objects.equals(checkTable[1], String.valueOf(opponentId)) && !Objects.equals(checkTable[2], String.valueOf(opponentId))){
            return -40;
        }
        return 0;
    }
    private String[][] checkForBigBoardChange(String[][] microBoard,int x,int y,String[][] bigTable){
        String[] checkTable = new String[3];
        for (int i=0;i<3;i++){ // check cols
            for(int j=0;j<3;j++){
                checkTable[j] = microBoard[i][j];
            }

            if (bigBoardChangesRowChecking(checkTable,x,y,bigTable)!=null){
                return bigBoardChangesRowChecking(checkTable,x,y,bigTable);
            }
        }
        for (int i=0;i<3;i++){ // check rows
            for(int j=0;j<3;j++){
                checkTable[j] = microBoard[j][i];
            }
            if (bigBoardChangesRowChecking(checkTable,x,y,bigTable)!=null){
                return bigBoardChangesRowChecking(checkTable,x,y,bigTable);
            }
        }
        for (int i = 0; i < 3; i++) { // check diagonal one
            checkTable[i] = microBoard[i][i];
        }
        if (bigBoardChangesRowChecking(checkTable,x,y,bigTable)!=null){
            return bigBoardChangesRowChecking(checkTable,x,y,bigTable);
        }

        for (int i = 2; i > -1; i--) { // check diagonal two
            checkTable[i] = microBoard[i][i];
        }
        if (bigBoardChangesRowChecking(checkTable,x,y,bigTable)!=null){
            return bigBoardChangesRowChecking(checkTable,x,y,bigTable);
        }
        return bigTable;
    }

    private String[][] bigBoardChangesRowChecking(String[] checkTable,int x,int y,String[][] bigBoard){
        if (Objects.equals(checkTable[0], String.valueOf(botId)) && Objects.equals(checkTable[1], String.valueOf(botId)) && Objects.equals(checkTable[2], String.valueOf(botId))) {
            bigBoard[x][y]=String.valueOf(botId);
            return bigBoard;
        }
        if (Objects.equals(checkTable[0], String.valueOf(opponentId)) && Objects.equals(checkTable[1], String.valueOf(opponentId)) && Objects.equals(checkTable[2], String.valueOf(opponentId))) {
            bigBoard[x][y]=String.valueOf(opponentId);
            return bigBoard;
        }
        return null;
    }
}
