package com.fumi.mybroadcast.statemachine.example;

import android.content.Context;
import android.util.Log;

import com.fumi.mybroadcast.service.MusicService;
import com.fumi.mybroadcast.statemachine.Action.FSMAction;
import com.fumi.mybroadcast.statemachine.FSM.FSM;
import com.fumi.mybroadcast.statemachine.States.FSMState;
import com.fumi.mybroadcast.statemachine.States.FSMStateAction;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

public class Example {
    private static final String TAG = Example.class.getSimpleName();

    private static String _configFileName = "config.xml";

    public static void testFSM(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(_configFileName);
            FSM f = new FSM(inputStream, new FSMAction() {
                @Override
                public boolean action(String curState, String message, String nextState, Object args) {
                    /*
                     * Here we can implement our login of how we wish to handle
                     * an action
                     */
                    StringBuffer sb = new StringBuffer();
                    sb.append("FSMAction: ");
                    sb.append(curState).append(":").append(message).append(args!=null?args.toString() : "");
                    Log.d(TAG, sb.toString());

                    return true;
                }
            });
            List<FSMState> states = f.getAllStates();
            for(FSMState state : states) {
                state.setBeforeTransition(new FSMStateAction() {
                    @Override
                    public void stateTransition(String state, Object arg) {
                        StringBuffer sb = new StringBuffer();
                        sb.append("BeforeTransition: ");
                        sb.append(state).append(arg!=null?arg.toString() : "");
                        Log.d(TAG, sb.toString());
                    }
                });

                state.setAfterTransition(new FSMStateAction() {
                    @Override
                    public void stateTransition(String state, Object arg) {
                        StringBuffer sb = new StringBuffer();
                        sb.append("AfterTransition: ");
                        sb.append(state).append(arg!=null?arg.toString() : "");
                        Log.d(TAG, sb.toString());
                    }
                });
            }

            Log.d(TAG, f.getCurrentState());
            f.ProcessFSM("MOVELEFT");
            Log.d(TAG, f.getCurrentState());
            f.ProcessFSM("MOVE");
            Log.d(TAG, f.getCurrentState());
            f.ProcessFSM("MOVERIGHT");
            Log.d(TAG, f.getCurrentState());
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Example.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Example.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Example.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void excute(Context context) {
        try {
            testFSM(context);
        } catch (Exception ex) {
            Logger.getLogger(Example.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}