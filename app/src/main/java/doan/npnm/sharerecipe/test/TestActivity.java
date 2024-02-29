package doan.npnm.sharerecipe.test;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.activity.user.MainActivity;
import doan.npnm.sharerecipe.app.context.AppContext;
import doan.npnm.sharerecipe.databinding.ActivityTestBinding;
import doan.npnm.sharerecipe.dialog.NotificationDialog;
import doan.npnm.sharerecipe.model.Category;
import doan.npnm.sharerecipe.model.recipe.CookTime;
import doan.npnm.sharerecipe.model.recipe.Directions;
import doan.npnm.sharerecipe.model.recipe.Ingredients;
import doan.npnm.sharerecipe.model.recipe.PrepareTime;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;

public class TestActivity extends AppCompatActivity {
    ActivityTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }



    private Recipe getRcp() {
        return new Recipe() {{
            Id = "Recipe_001";
            Name = "Cá bọc bột rán giòn xào cà tím";
            Description = "Thịt cá ngọt được bọc trong một lớp bột vàng giòn, xào cùng với cà tím, bí ngồi, ăn không ngấy như món cá rán thông thường.";
            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://i1-ngoisao.vnecdn.net/2013/09/10/ca1-4367-1378804996.jpg?w=680&h=0&q=100&dpr=1&fit=crop&s=9P178VGqgPk2v6oybK8N2g";
            RecipeAuth = "user_id_test_member1";
            PrepareTime = new PrepareTime() {{
                Time = "10";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "30";
                TimeType = "m";
            }};
            Level = "Difficult";
            Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                add(new Ingredients() {{
                    Id = 1;
                    Name = "Phi lê cá";
                    Quantitative = 400;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Cà tím";
                    Quantitative = 100;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Bí ngòi";
                    Quantitative = 100;
                }});

                add(new Ingredients() {{
                    Id = 4;
                    Name = "Bột nămg hoặc ngô";
                    Quantitative = 35;
                }});
                add(new Ingredients() {{
                    Id = 5;
                    Name = "Muối, dầu hào, xì dầu (nước tương), đường, hạt nêm, hạt tiêu, hành lá, tỏi";
                    Quantitative = 15;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "Phi lê cá rửa sạch, để ráo, cắt miếng vừa ăn, ướp vào cá một thìa nhỏ muối, nửa thìa nhỏ hạt nêm, một ít hạt tiêu, trộn đều, ướp khoảng 30 phút.";

                }});
                add(new Directions() {{
                    Id = 2;
                    Name = " Cà tím, bí ngồi rửa sạch, để ráo, cắt miếng vừa ăn.";
                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Cho bột năng vào âu cá và trộn đều, rũ bỏ những bột còn bám thừa xung quanh miếng cá.\n" +
                            "Tiếp theo đun nóng nồi nhỏ, cho cá vào rán vàng đều hai mặt, vớt cá ra đĩa có lót giấy thấm bớt dầu ăn.";

                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Đun nóng một ít dầu ăn, phi tỏi thơm, cho cà tím, bí ngồi vào xào, rưới vào một ít xì dầu, dầu hào, nửa thìa nhỏ đường, xào đến khi cà tím và bí ngồi chín vừa ý.";

                }});
                add(new Directions() {{
                    Id = 5;
                    Name = "ho tiếp cá đã rán vào, xào nhanh tay, đảo đều, đậy kín nắp nồi khoảng 5 phút tiếp theo mở nắp ra, đun tiếp khoảng 5 phút nữa. Nếu khô bạn có thể rưới vào một ít xì dầu và nước lọc, nêm nếm lại gia vị tùy theo sở thích của bạn. Tắt bếp, thêm hành lá thái nhỏ, dùng làm món mặn ăn với cơm.";

                }});
            }};
            View = 0;
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=");
            }};

            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = new ArrayList<>();
            ImagePreview = new ArrayList<String>() {{
                add("https://i1-ngoisao.vnecdn.net/2013/09/10/ca3-8382-1378805776.jpg?w=0&h=0&q=100&dpr=1&fit=crop&s=aaKX_AyHxICsPmtJR5s4tg");
                add("https://i1-ngoisao.vnecdn.net/2013/09/10/ca4-7404-1378805777.jpg?w=0&h=0&q=100&dpr=1&fit=crop&s=hFJeJJPYJZoqNS-xDW4peA");
                add("https://i1-ngoisao.vnecdn.net/2013/09/10/ca5-6464-1378805777.jpg?w=0&h=0&q=100&dpr=1&fit=crop&s=9dZNg8dpvJ3Ng-AhUHWqEQ");
                add("https://i1-ngoisao.vnecdn.net/2013/09/10/ca7-5617-1378805777.jpg?w=0&h=0&q=100&dpr=1&fit=crop&s=Mo2rH8pttH6tMPWt9AoycQ");
            }};
        }};
    }

}