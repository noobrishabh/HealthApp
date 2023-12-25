package com.example.healthapp.activities

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthapp.Constants
import com.example.healthapp.R
import com.example.healthapp.databinding.ActivityWorkoutBinding
import java.lang.Exception
import java.util.Locale


class WorkoutActivity : BaseActivity(), TextToSpeech.OnInitListener {
    private var binding: ActivityWorkoutBinding?=null

    private var restTimer: CountDownTimer?=null
    private var restProgress=0
    private var restTimerDuration:Long=1
    private var exerciseTimerDuration:Long=1
    private var exerciseTimer: CountDownTimer?=null
    private var exerciseProgress=0

    private var exerciseList:ArrayList<ExerciseModel>?=null
    private var currentExercisePosition=-1



    private var tts: TextToSpeech?=null
    private var player: MediaPlayer?=null

    private var exerciseAdapter:WorkoutStatusAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWorkoutBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if (supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        exerciseList= Constants.defaultExerciseList()

        tts= TextToSpeech(this,this)

        binding?.toolbarExercise?.setNavigationOnClickListener{
            onBackPressed()
        }

        setUpRestView()
        setUpExerciseRecyclerView()


    }
    private fun setUpExerciseRecyclerView(){
        binding?.rvExerciseStatus?.layoutManager=
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)

        exerciseAdapter= WorkoutStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter=exerciseAdapter
    }

    private fun setUpRestView(){

        try {
            val soundURI= Uri.parse("android.resources//com.example.health app/"+R.raw.press_start)
            player= MediaPlayer.create(applicationContext,soundURI)
            player?.isLooping=false
            player?.start()
        }catch (e: Exception){
            e.printStackTrace()
        }
        binding?.flRestView?.visibility= View.VISIBLE
        binding?.tvTitle?.visibility= View.VISIBLE
        binding?.tvExercise?.visibility= View.INVISIBLE
        binding?.flExerciseView?.visibility= View.INVISIBLE
        binding?.ivImage?.visibility= View.INVISIBLE
        binding?.tvUpcomingLable?.visibility= View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility= View.VISIBLE
        if (restTimer!=null){
            restTimer?.cancel()
            restProgress=0
        }
        binding?.tvUpcomingExerciseName?.text=
            exerciseList!![currentExercisePosition + 1].getName()
        setRestProgressBar()
    }

    private fun setUpExerciseView(){
        binding?.flRestView?.visibility= View.INVISIBLE
        binding?.tvTitle?.visibility= View.INVISIBLE
        binding?.tvExercise?.visibility= View.VISIBLE
        binding?.flExerciseView?.visibility= View.VISIBLE
        binding?.ivImage?.visibility= View.VISIBLE
        binding?.tvUpcomingLable?.visibility= View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility= View.INVISIBLE

        if(exerciseTimer!=null){
            exerciseTimer?.cancel()
            exerciseProgress=0
        }

        speakOut(exerciseList!![currentExercisePosition].getName())

        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExercise?.text=exerciseList!![currentExercisePosition].getName()

        setExerciseProgressBar()



    }

    private fun setRestProgressBar(){
        binding?.progressBar?.progress=restProgress

        restTimer=object: CountDownTimer(restTimerDuration*1000,1000){
            override fun onTick(p0: Long) {
                restProgress++
                binding?.progressBar?.progress=10-restProgress
                binding?.tvTimer?.text=(10-restProgress).toString()
            }
            override fun onFinish() {
                currentExercisePosition++
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setUpExerciseView()
            }
        }.start()
    }

    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress=exerciseProgress

        exerciseTimer=object: CountDownTimer(exerciseTimerDuration*1000,1000){
            override fun onTick(p0: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress=30-exerciseProgress
                binding?.tvTimerExercise?.text=(30-exerciseProgress).toString()
            }
            override fun onFinish() {

                if(currentExercisePosition<exerciseList?.size!!-1){
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    setUpRestView()
                }else{

                    Toast.makeText(
                        this@WorkoutActivity,
                        "Congratulation you have completed the workout.",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent= Intent(this@WorkoutActivity,MainActivity2::class.java)
                    startActivity(intent)

                }
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (restTimer!=null){
            restTimer?.cancel()
            restProgress=0
        }

        if(exerciseTimer!=null){
            exerciseTimer?.cancel()
            exerciseProgress=0
        }
        if(player!=null){
            player!!.stop()
        }
        binding=null
    }

    override fun onInit(status: Int) {
        if(status== TextToSpeech.SUCCESS){
            val result=tts!!.setLanguage(Locale.ENGLISH)

            if(result== TextToSpeech.LANG_MISSING_DATA || result== TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS","The Language Specified is not supported")
            }
        }else{
            Log.e("TTS","Initialization Failed")
        }

    }

    private fun speakOut(text:String){
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH,null,"")
    }
}