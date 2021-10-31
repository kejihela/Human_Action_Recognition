package com.example.technocolab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.technocolab.ml.ConvertedModel;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class MainActivity extends AppCompatActivity  {

    private ImageView imgview;
    private Button select,predict,Camera,predict_Y;
    private TextView tv,tv2;
    private Bitmap img;
    private Uri VideoPath;
    private EditText Y_L;
    private String T_L_String;
    static final int REQUEST_VIDEO_CAPTURE = 1;



    ////////////////////try/////////////////////

    ////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgview=(ImageView) findViewById(R.id.imageView);
        select=(Button) findViewById(R.id.button);
        predict=(Button) findViewById(R.id.button2);
        Camera=(Button) findViewById(R.id.cam_button);
        tv=(TextView) findViewById(R.id.textView);
        tv2=(TextView) findViewById(R.id.textView2);
        Y_L=(EditText) findViewById(R.id.Text_BOX);
        predict_Y=(Button) findViewById(R.id.Youtube_button);

        Camera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent( MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);
                startActivityForResult(intent,1);

            }

        });

        predict_Y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent,5);

            }
        });

        select.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,100);


            }

        });

        predict.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                img = Bitmap.createScaledBitmap(img,150,150,true);



                try {
                    ConvertedModel model = ConvertedModel.newInstance(getApplicationContext());

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 150, 150, 3}, DataType.FLOAT32);
                    TensorImage tensorImage= new TensorImage(DataType.FLOAT32);
                    tensorImage.load(img);
                    ByteBuffer byteBuffer =tensorImage.getBuffer();
                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    ConvertedModel.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    // Releases model resources if no longer used.
                    model.close();

                    int L_index=0;
                    for(int i=0;i<outputFeature0.getFloatArray().length;i++)
                    {
                        if(outputFeature0.getFloatArray()[i]>outputFeature0.getFloatArray()[L_index])
                        {
                            L_index=i;
                        }

                    }

                    switch(L_index) {
                        case 0:
                            tv.setText("breakdancing");
                            break;
                        case 1:
                            tv.setText("calligraphy");
                            break;
                        case 2:
                            tv.setText("celebrating");
                            break;
                        case 3:
                            tv.setText("claypotterymaking");
                            break;
                        case 4:
                            tv.setText("climbingarope");
                            break;
                        case 5:
                            tv.setText("cookingoncampfire");
                            break;
                        case 6:
                            tv.setText("eatingicecream");
                            break;
                        case 7:
                            tv.setText("golfdriving");
                            break;
                        case 8:
                            tv.setText("pushup");
                            break;
                        case 9:
                            tv.setText("raisingeyebrows");
                            break;
                        case 10:
                            tv.setText("ridingscooter");
                            break;
                        default:
                            tv.setText("NONE");

                    }

                } catch (IOException e) {
                    // TODO Handle the exception
                }

            }
        });
    }


    protected void PredictYoutube(String Link)
    {
        ////////first download video /////////////////////

        URL u = null;
        InputStream is = null;

        try {
            u = new URL(Uri.decode("https://www.youtube.com/watch?v=oCYq2lmlIC8"));
            is = u.openStream();
            HttpURLConnection huc = (HttpURLConnection)u.openConnection(); //to know the size of video
            int size = huc.getContentLength();

            if(huc != null) {
                String fileName = "FILE.mp4";
                String storagePath = Environment.getExternalStorageDirectory().toString();
                File f = new File(storagePath,fileName);

                FileOutputStream fos = new FileOutputStream(f);
                byte[] buffer = new byte[1024];
                int len1 = 0;
                if(is != null) {
                    while ((len1 = is.read(buffer)) > 0) {
                        fos.write(buffer,0, len1);
                    }
                }
                if(fos != null) {
                    fos.close();
                }
            }
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if(is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
                // just going to ignore this one
            }
        }

        //VideoPath=//
        ////////////////////////////////////////////////


        ///////pridict human action////////////////////
        PredictRes(VideoPath);


        ////////////////////////////////////////////////

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==100)
        {
            imgview.setImageURI(data.getData());
            Uri ur=data.getData();
            try
            {
                img= MediaStore.Images.Media.getBitmap(this.getContentResolver(),ur);
            }
            catch (IOException e)
            {
                e.printStackTrace();

            }

        }

        else if(requestCode==1)
        {

            VideoPath=data.getData();
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            VideoView videoView = new VideoView(this);
            videoView.setVideoURI(VideoPath);
            videoView.start();
            builder.setView(videoView).show();
            PredictRes(VideoPath);


            //imgview.setImageBitmap(bmp);


            //////////////////////////////////////////////


        }
        else if(requestCode==5) {
            VideoPath=data.getData();
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            VideoView videoView = new VideoView(this);
            videoView.setVideoURI(VideoPath);
            videoView.start();
            builder.setView(videoView).show();
            PredictRes(VideoPath);
            //T_L_String=Y_L.getText().toString();
            //tv.setText(T_L_String);
            //PredictYoutube(T_L_String);

        }
    }

    protected void PredictRes(Uri path)
    {
        int res=0,res_BD=0,res_call=0,res_celeb=0,res_calyp=0,res_clim=0,res_cook=0,res_eat=0,res_golf=0,res_push=0,res_rais=0,res_riding=0;
        int numberOfFrame=0;

        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();

        try
        {
            //path of the video of which you want frames
            retriever.setDataSource(getApplicationContext(),path);
        } catch (Exception e)
        {
            System.out.println("Exception= "+e);
        }
        long duration = retriever.getMetadata().getLong("duration");
        double frameRate = retriever.getMetadata().getDouble("framerate");
        //numberOfFrame = (int) (duration/frameRate);
        numberOfFrame = (int) (duration/1000);



        for(int i=1;i<=numberOfFrame;i++)
        {

            Bitmap bmp = retriever.getFrameAtTime(i*1000000);
            img= Bitmap.createScaledBitmap(bmp,150,150,true);
            //imgview.setImageBitmap(img);

            try {
                ConvertedModel model = ConvertedModel.newInstance(getApplicationContext());

                // Creates inputs for reference.
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 150, 150, 3}, DataType.FLOAT32);
                TensorImage tensorImage= new TensorImage(DataType.FLOAT32);
                tensorImage.load(img);
                ByteBuffer byteBuffer =tensorImage.getBuffer();
                inputFeature0.loadBuffer(byteBuffer);

                // Runs model inference and gets result.
                ConvertedModel.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                // Releases model resources if no longer used.
                model.close();

                int L_index=0;
                for(int j=0;j<outputFeature0.getFloatArray().length;j++)
                {
                    if(outputFeature0.getFloatArray()[j]>outputFeature0.getFloatArray()[L_index])
                    {
                        L_index=j;
                    }

                }

                switch(L_index) {
                    case 0:
                        res_BD++;
                        break;
                    case 1:
                        res_call++;
                        break;
                    case 2:
                        res_celeb++;
                        break;
                    case 3:
                        res_calyp++;
                        break;
                    case 4:
                        res_clim++;
                        break;
                    case 5:
                        res_cook++;
                        break;
                    case 6:
                        res_eat++;
                        break;
                    case 7:
                        res_golf++;
                        break;
                    case 8:
                        res_push++;
                        break;
                    case 9:
                        res_rais++;
                        break;
                    case 10:
                        res_riding++;
                        break;
                    default:
                        res++;
                }

            } catch (IOException e) {
                // TODO Handle the exception
            }


        }

        ////////////////do result here////////////////
        tv2.setText("BD"+res_BD+"cli"+res_call+"cel"+res_celeb+"caly"+res_calyp+"clmb"+res_clim+"cook"+res_cook+"eat"+res_eat+"glf"+res_golf+"psh"+res_push +"eye"+res_rais+"rid"+res_riding);
        if(res_BD>=res_call&&res_BD>=res_celeb&&res_BD>=res_calyp&&res_BD>=res_clim&&res_BD>=res_cook&&res_BD>=res_eat&&res_BD>=res_golf&&res_BD>=res_push&&res_BD>=res_rais&&res_BD>=res_riding)
        {
            res= (res_BD*100/numberOfFrame);
            tv.setText("breakdancing"+res+"%");
        }
        else if(res_call>=res_BD&&res_call>=res_celeb&&res_call>=res_calyp&&res_call>=res_clim&&res_call>=res_cook&&res_call>=res_eat&&res_call>=res_golf&&res_call>=res_push&&res_call>=res_rais&&res_call>=res_riding)
        {
            res= (res_call*100/numberOfFrame);
            tv.setText("calligraphy"+res+"%");
        }
        else if(res_celeb>=res_BD&&res_celeb>=res_call&&res_celeb>=res_calyp&&res_celeb>=res_clim&&res_celeb>=res_cook&&res_celeb>=res_eat&&res_celeb>=res_golf&&res_celeb>=res_push&&res_celeb>=res_rais&&res_celeb>=res_riding)
        {
            res= (res_celeb*100/numberOfFrame);
            tv.setText("celebrating"+res+"%");
        }
        else if(res_calyp>=res_BD&&res_calyp>=res_celeb&&res_calyp>=res_call&&res_calyp>=res_clim&&res_calyp>=res_cook&&res_calyp>=res_eat&&res_calyp>=res_golf&&res_calyp>=res_push&&res_calyp>=res_rais&&res_calyp>=res_riding)
        {
            res= (res_calyp*100/numberOfFrame);
            tv.setText("claypotterymaking"+res+"%");
        }
        else if(res_clim>=res_BD&&res_clim>=res_celeb&&res_clim>=res_calyp&&res_clim>=res_call&&res_clim>=res_cook&&res_clim>=res_eat&&res_clim>=res_golf&&res_clim>=res_push&&res_clim>=res_rais&&res_clim>=res_riding)
        {
            res= (res_clim*100/numberOfFrame);
            tv.setText("climbingarope"+res+"%");
        }
        else if(res_cook>=res_BD&&res_cook>=res_celeb&&res_cook>=res_calyp&&res_cook>=res_clim&&res_cook>=res_call&&res_cook>=res_eat&&res_cook>=res_golf&&res_cook>=res_push&&res_cook>=res_rais&&res_cook>=res_riding)
        {
            res= (res_cook*100/numberOfFrame);
            tv.setText("cookingoncampfire"+res+"%");
        }
        else if(res_eat>=res_BD&&res_eat>=res_celeb&&res_eat>=res_calyp&&res_eat>=res_clim&&res_eat>=res_cook&&res_eat>=res_call&&res_eat>=res_golf&&res_eat>=res_push&&res_eat>=res_rais&&res_eat>=res_riding)
        {
            res= (res_eat*100/numberOfFrame);
            tv.setText("eatingicecream"+res+"%");
        }
        else if(res_golf>=res_BD&&res_golf>=res_celeb&&res_golf>=res_calyp&&res_golf>=res_clim&&res_golf>=res_cook&&res_golf>=res_call&&res_golf>=res_eat&&res_golf>=res_push&&res_golf>=res_rais&&res_golf>=res_riding)
        {
            res= (res_golf*100/numberOfFrame);
            tv.setText("golfdriving"+res+"%");
        }
        else if(res_push>=res_BD&&res_push>=res_celeb&&res_push>=res_calyp&&res_push>=res_clim&&res_push>=res_cook&&res_push>=res_call&&res_push>=res_golf&&res_push>=res_eat&&res_push>=res_rais&&res_push>=res_riding)
        {
            res= (res_push*100/numberOfFrame);
            tv.setText("pushup"+res+"%");
        }
        else if(res_rais>=res_BD&&res_rais>=res_celeb&&res_rais>=res_calyp&&res_rais>=res_clim&&res_rais>=res_cook&&res_rais>=res_call&&res_rais>=res_golf&&res_rais>=res_push&&res_rais>=res_eat&&res_rais>=res_riding)
        {
            res= (res_rais*100/numberOfFrame);
            tv.setText("raisingeyebrows"+res+"%");
        }
        else
        {
            res= (res_riding*100/numberOfFrame);
            tv.setText("ridingscooter"+res+"%");
        }
    }


}
