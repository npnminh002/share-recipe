package doan.npnm.sharerecipe.activity;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.app.context.AppContext;
import doan.npnm.sharerecipe.base.BaseActivity;
import doan.npnm.sharerecipe.databinding.ActivityAddDataBinding;
import doan.npnm.sharerecipe.model.Category;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.recipe.CookTime;
import doan.npnm.sharerecipe.model.recipe.Directions;
import doan.npnm.sharerecipe.model.recipe.Ingredients;
import doan.npnm.sharerecipe.model.recipe.PrepareTime;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.model.recipe.RecipeAuth;
import doan.npnm.sharerecipe.utility.Constant;

public class AddDataActivity extends BaseActivity<ActivityAddDataBinding> {
    @Override
    protected ActivityAddDataBinding getViewBinding() {
        return ActivityAddDataBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void createView() {
        
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void OnClick() {

        binding.btnAddUsers.setOnClickListener(v -> generateDefaultUsersList().forEach(item -> {
            firestore.collection(Constant.KEY_USER)
                    .document(item.UserID)
                    .set(item)
                    .addOnSuccessListener(task -> {
                        onResult("Add User Success: " + item.UserID);
                    })
                    .addOnFailureListener(e -> {
                        onResult("Error: " + e.getMessage());
                    });

        }));

        binding.btnAddRecipe.setOnClickListener(v -> getRecipeData().forEach(recipe -> {

            firestore.collection(Constant.RECIPE)
                    .document(recipe.Id)
                    .set(recipe)
                    .addOnSuccessListener(task -> {
                        onResult("Success");
                    }).addOnFailureListener(e -> {
                        onResult(e.getMessage());
                    });

        }));

    }

    String value = "";

    void onResult(Object value) {
        this.value += "\n" + value;
        binding.txtResult.setText(this.value);
    }

    public ArrayList<Users> generateDefaultUsersList() {
        ArrayList<Users> userList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            userList.add(new Users(
                    "user_id_test_member" + i,
                    "UserName" + i,
                    "email" + i + "@example.com",
                    "password" + i,
                    "token" + i,
                    "https://img.freepik.com/premium-vector/chef-icon-illustraton-vector_57048-38.jpg?w=740",
                    "" + System.currentTimeMillis(),
                    "address" + i,
                    "gender" + i,
                    "NickName" + i,
                    i * 100, // Follower
                    i * 50,  // Follow
                    i * 10,  // Recipe
                    i % 3    // AccountType (just an example)
            ));
        }
        return userList;
    }

    public ArrayList<Category> getListCategory() {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category("1", AppContext.getContext().getString(R.string.bakery), R.drawable.category_bakery));
        categories.add(new Category("2", AppContext.getContext().getString(R.string.beverages), R.drawable.category_beverages));
        categories.add(new Category("3", AppContext.getContext().getString(R.string.dairy), R.drawable.category_dairy));
        categories.add(new Category("4", AppContext.getContext().getString(R.string.frozen), R.drawable.category_frozen));
        categories.add(new Category("5", AppContext.getContext().getString(R.string.fruit), R.drawable.category_fruit));
        categories.add(new Category("6", AppContext.getContext().getString(R.string.meat), R.drawable.category_meat));
        categories.add(new Category("7", AppContext.getContext().getString(R.string.poultry), R.drawable.category_poultry));
        categories.add(new Category("8", AppContext.getContext().getString(R.string.seafood), R.drawable.category_seafood));
        categories.add(new Category("9", AppContext.getContext().getString(R.string.vegetable), R.drawable.category_vegettable));
        return categories;

    }

//    ArrayList<String> data = new ArrayList<>();


    public ArrayList<Recipe> getRecipeData() {
        ArrayList<Recipe> listData = new ArrayList<>();
        listData.add(new Recipe() {{
            Id = "Recipe_001";
            Name = "Cá bọc bột rán giòn xào cà tím";
            Description = "Thịt cá ngọt được bọc trong một lớp bột vàng giòn, xào cùng với cà tím, bí ngồi, ăn không ngấy như món cá rán thông thường.";
            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://i1-ngoisao.vnecdn.net/2013/09/10/ca1-4367-1378804996.jpg?w=680&h=0&q=100&dpr=1&fit=crop&s=9P178VGqgPk2v6oybK8N2g";
            RecipeAuth = new RecipeAuth() {{
                AuthId = "user_id_test_member_1";
                AuthName = "Jame";
                Gender = "Male";
                Address = "Viet Nam";
                NickName = "#" + AuthName.toLowerCase();
                Image = "https://img.freepik.com/premium-vector/chef-icon-illustraton-vector_57048-38.jpg?w=740";
            }};
            PrepareTime = new PrepareTime() {{
                Time = "10";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "30";
                TimeType = "m";
            }};
            Level = "Easy";
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
            Love = 0;
            IsConfirm = false;
            Category = getListCategory().get(7);
            ImagePreview = new ArrayList<String>() {{
                add("https://i1-ngoisao.vnecdn.net/2013/09/10/ca3-8382-1378805776.jpg?w=0&h=0&q=100&dpr=1&fit=crop&s=aaKX_AyHxICsPmtJR5s4tg");
                add("https://i1-ngoisao.vnecdn.net/2013/09/10/ca4-7404-1378805777.jpg?w=0&h=0&q=100&dpr=1&fit=crop&s=hFJeJJPYJZoqNS-xDW4peA");
                add("https://i1-ngoisao.vnecdn.net/2013/09/10/ca5-6464-1378805777.jpg?w=0&h=0&q=100&dpr=1&fit=crop&s=9dZNg8dpvJ3Ng-AhUHWqEQ");
                add("https://i1-ngoisao.vnecdn.net/2013/09/10/ca7-5617-1378805777.jpg?w=0&h=0&q=100&dpr=1&fit=crop&s=Mo2rH8pttH6tMPWt9AoycQ");
            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_0030";
            Name = "Mì ý xào rau củ";
            Description = "Đổi vị cho cả nhà với món mì Ý xào rau củ giòn ngon, không chỉ lạ miệng lại có thể dễ dàng cung cấp chất xơ cho gia đình.";
            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/2021/10/CookDish/cach-lam-mi-y-xao-rau-cu-gion-ngon-chay-man-deu-dung-don-gian-avt-1200x676.jpg";
            RecipeAuth = new RecipeAuth() {{
                AuthId = "user_id_test_member_11";
                AuthName = "Jame";
                Gender = "Male";
                Address = "Viet Nam";
                NickName = "#" + AuthName.toLowerCase();
                Image = "https://img.freepik.com/premium-vector/chef-icon-illustraton-vector_57048-38.jpg?w=740";
            }};
            PrepareTime = new PrepareTime() {{
                Time = "15";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "30";
                TimeType = "m";
            }};
            Level =  "Easy";
            Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                add(new Ingredients() {{
                    Id = 1;
                    Name = "Mỳ ý";
                    Quantitative = 120;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Cà chua";
                    Quantitative = 300;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Cà rốt";
                    Quantitative = 100;
                }});

                add(new Ingredients() {{
                    Id = 4;
                    Name = "Khoai tây";
                    Quantitative = 250;
                }});
                add(new Ingredients() {{
                    Id = 5;
                    Name = "Tương cà 2 muỗng canh\n" +
                            " Dầu ăn 4 muỗng canh\n" +
                            " Hạt nêm chay 1/2 muỗng canh";
                    Quantitative = 15;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "Khoai tây và cà rốt sau khi mua về thì rửa với nước cho hết bùn đất, tiến hành gọt bỏ vỏ đi, rửa lại với nước sạch lần nữa và để ráo.\n" +
                            "\n" +
                            "Tiếp theo, cắt khoai tây và cà rốt thành hình hạt lựu nhỏ. Vì cà rốt lâu chín nên hãy cắt cà rốt mỏng và nhỏ hơn khoai tây.\n" +
                            "\n" +
                            "Rửa sạch 2 quả cà chua, sau đó băm nhuyễn.\n" +
                            "\n";
                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "cho khoảng 700ml nước vào cùng với 1 muỗng canh dầu ăn và 1 muỗng cà phê muối.\n" +
                            "\n" +
                            "Thấy nước hơi sôi lên thì cho hết 120gr mì Ý vào, giảm lại lửa vừa, khi đầu mì chạm nước đã mềm thì dùng tay nhấn hết toàn bộ sợi mì chìm xuống nước.";
                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Đặt chảo lên bếp bật lửa vừa, cho vào 3 muỗng canh dầu ăn chờ dầu nóng thì cho tiếp 1 muỗng canh hành tím băm phi thơm.\n" +
                            "\n" +
                            "Thấy hành tím thơm thì cho hết số cà chua đã băm vào, xào khoảng 3 phút thì cho khoảng 1 chén nước lọc (chén ăn cơm) vào chảo cà chua.\n" +
                            "\n" +
                            "Hỗn hợp cà chua vừa sôi lên thì cho cà rốt cắt nhỏ vào, đảo đều tay khoảng 5 phút thì cho hết số khoai tây nấu cùng, nêm nếm với 1 muỗng cà phê hạt nêm chay, 1/2 muỗng cà phê bột ngọt, 1 muỗng canh đường, 1 muỗng cà phê muối và 2 muỗng canh tương cà.";
                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Chờ hỗn hợp sôi lên lần nữa thì nêm nếm lại cho vừa khẩu vị rồi tắt bếp. Rắc vào khoảng 1/2 muỗng cà phê tiêu xay cho thơm.";
                }});
            }};
            View = 0;
            Love = 0;
            IsConfirm = false;
            Category = getListCategory().get(7);
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/2021/10/CookRecipe/GalleryStep/thanh-pham-205.jpg");
                add("https://cdn.tgdd.vn/2021/10/CookRecipe/GalleryStep/thanh-pham-206.jpg");
            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_002";
            Name = "Mực ống xào với dứa và hành tây";
            Description = "Mực là một loại hải sản quen thuộc, giàu dinh dưỡng và có thể chế biến thành rất nhiều món ăn ngon, nhất là món xào.";
            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/2021/08/CookRecipe/Avatar/muc-xao-dua-thom-thumbnail-1.jpg";
            RecipeAuth = new RecipeAuth() {{
                AuthId = "user_id_test_member_2";
                AuthName = "Jame";
                Gender = "Male";
                Address = "Viet Nam";
                NickName = "#" + AuthName.toLowerCase();
                Image = "https://img.freepik.com/premium-vector/chef-icon-illustraton-vector_57048-38.jpg?w=740";
            }};
            PrepareTime = new PrepareTime() {{
                Time = "15";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "30";
                TimeType = "m";
            }};
            Level =  "Normal";
            Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                add(new Ingredients() {{
                    Id = 1;
                    Name = "Mực ống";
                    Quantitative = 500;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Dứa";
                    Quantitative = 100;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Hành tây";
                    Quantitative = 200;
                }});

                add(new Ingredients() {{
                    Id = 4;
                    Name = "Hành tím";
                    Quantitative = 35;
                }});
                add(new Ingredients() {{
                    Id = 5;
                    Name = "Hành lá";
                    Quantitative = 20;
                }});
                add(new Ingredients() {{
                    Id = 6;
                    Name = "Muối, dầu hào, xì dầu (nước tương), đường, hạt nêm, hạt tiêu, tỏi";
                    Quantitative = 15;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "Mực làm sạch nội tạng bên trong, rửa sạch với nước, cắt thành từng miếng vừa ăn khoảng 1 lóng tay rồi để ráo.";
                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Hành tây, hành tím cắt bỏ gốc, lột vỏ. Đối với hành tây thì cắt múi cau, hành tím bạn cắt lát mỏng.";
                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Ướp mực với 2 muỗng canh nước mắm, 1 muỗng cà phê hạt nêm, 1/2 muỗng cà phê bột ngọt, 1 muỗng cà phê tiêu xay, 1/2 phần hành tím đã cắt lát mỏng.";

                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Bắc chảo lên bếp mở lửa vừa, cho 2 muỗng canh dầu ăn và phần hành tím đã cắt lát mỏng còn lại vào chảo phi vàng thơm.";
                }});
                add(new Directions() {{
                    Id = 5;
                    Name = "Đảo đều khoảng 10 phút để các nguyên liệu chín và thấm gia vị, nêm nếm lại cho vừa miệng. Cuối cùng bạn cho hành lá đã cắt nhỏ và rắc thêm ít tiêu xay lên trên rồi đảo 1 vòng, tắt bếp là xong.";

                }});
            }};
            View = 0;
            Love = 0;
            IsConfirm = false;
            Category = getListCategory().get(7);
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/2021/07/CookRecipe/GalleryStep/thanh-pham-1497.jpg");
                add("https://cdn.tgdd.vn/2021/07/CookRecipe/GalleryStep/thanh-pham-1498.jpg");
                add("https://cdn.tgdd.vn/2021/10/CookRecipe/Avatar/muc-nang-xao-voi-dua-va-hanh-t-y-thumbnail.jpg");

            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_003";
            Name = "Thịt bò xào hành cần tây";
            Description = "Các món xào luôn được nhiều gia đình ưa chuộng không chỉ trong những bữa cơm gia đình mà còn ở những bữa tiệc bởi vì tính bổ dưỡng và thơm ngon của nó. Hôm nay, mình sẽ hướng dẫn bạn cách làm thịt bò xào hành cần tây cực thơm ngon và bổ dưỡng nhé.";
            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/2021/05/CookProduct/CachlamBOXAOCANTAYngonngatngaybovuamemvuathamBepCuaVo0-18screenshot-1200x676.jpg";
            RecipeAuth = new RecipeAuth() {{
                AuthId = "user_id_test_member_3";
                AuthName = "Jame";
                Gender = "Male";
                Address = "Viet Nam";
                NickName = "#" + AuthName.toLowerCase();
                Image = "https://img.freepik.com/premium-vector/chef-icon-illustraton-vector_57048-38.jpg?w=740";
            }};
            PrepareTime = new PrepareTime() {{
                Time = "15";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "20";
                TimeType = "m";
            }};
            Level =  "Normal";
            Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                add(new Ingredients() {{
                    Id = 1;
                    Name = "Thịt bò";
                    Quantitative = 400;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Cần tây";
                    Quantitative = 100;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Hành tây";
                    Quantitative = 200;
                }});

                add(new Ingredients() {{
                    Id = 4;
                    Name = "Ngò rí";
                    Quantitative = 35;
                }});

                add(new Ingredients() {{
                    Id = 5;
                    Name = "Muối, dầu hào, xì dầu (nước tương), đường, hạt nêm, hạt tiêu, tỏi";
                    Quantitative = 15;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "Thịt bò mua về dùng muối chà xát, sau đó rửa sạch lại với nước, cắt lát mỏng rồi cho vào tô.";
                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Cho vào 1 muỗng cà phê nước tương, 1 muỗng cà phê dầu hào, 1 muỗng cà phê dầu ăn, 1 muỗng cà phê bột ngọt và 1/2 muỗng cà phê đường.";
                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Cà chua, hành tây rửa sạch, cắt múi cau. Cần tây sau khi rửa sạch thì cắt thành khúc dài khoảng 2 đốt ngón tay.";

                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Bắc chảo lên bếp, cho vào chảo 1 muỗng canh dầu ăn và phi tỏi cho thơm. Sau đó cho thịt bò vào chảo, xào nhanh tay khi thịt bò còn tái thì cho thịt bò ra chén.";
                }});
                add(new Directions() {{
                    Id = 5;
                    Name = "Cho cần tây vào xào tiếp tục. Khi thấy cần tây đã gần chín thì cho cà chua vào đảo đều và nêm nếm lại gia vị cho vừa ăn.";

                }});
                add(new Directions() {{
                    Id = 6;
                    Name = "Khi thấy cà chua đã gần chín thì cho hành tây vào. Khi hành tây chín thì bạn rắc thêm một ít tiêu nữa là hoàn thành.";

                }});
            }};
            View = 0;
            Love = 0;
            IsConfirm = false;
            Category = getListCategory().get(7);
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/2021/05/CookRecipe/GalleryStep/thanh-pham-68.jpg");
                add("https://cdn.tgdd.vn/2021/05/CookProduct/CachlamBOXAOCANTAYngonngatngaybovuamemvuathamBepCuaVo0-18screenshot-1200x676.jpg");

            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_004";
            Name = "Thịt kho tàu";
            Description = "Thịt heo là nguyên liệu chính trong rất nhiều món ăn, một trong những món ngon từ thịt heo phổ biến nhất chính là thịt kho trứng. Ai cũng có bí quyết kho thịt riêng, nhưng bạn có chắc rằng mình biết cách kho thịt kho tàu ngon đúng điệu không?";
            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/2021/01/CookRecipe/GalleryStep/thanh-pham-269.jpg";
            RecipeAuth = new RecipeAuth() {{
                AuthId = "user_id_test_member_4";
                AuthName = "Jame";
                Gender = "Male";
                Address = "Viet Nam";
                NickName = "#" + AuthName.toLowerCase();
                Image = "https://img.freepik.com/premium-vector/chef-icon-illustraton-vector_57048-38.jpg?w=740";
            }};
            PrepareTime = new PrepareTime() {{
                Time = "60";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "45";
                TimeType = "m";
            }};
            Level =  "Easy";
            Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                add(new Ingredients() {{
                    Id = 1;
                    Name = "Thịt ba chỉ hay thịt chân giò";
                    Quantitative = 500;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Trứng vịt luộc";
                    Quantitative = 300;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Nước dừa";
                    Quantitative = 400;
                }});

                add(new Ingredients() {{
                    Id = 4;
                    Name = "Hành băm";
                    Quantitative = 20;
                }});

                add(new Ingredients() {{
                    Id = 5;
                    Name = "Muối, dầu hào, xì dầu (nước tương), đường, hạt nêm, hạt tiêu, tỏi";
                    Quantitative = 15;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "Thịt ba chỉ hay thịt chân giò sau khi mua về thì rửa sạch với nước muối, để ráo sau đó cắt miếng vừa ăn rồi cho vào tô, nên chọn thịt có da mỏng để kho được ngon, mau mềm hơn và không bị ngấy.";
                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Lần lượt nêm vào tô 1 muỗng canh hành băm, 1 muỗng canh tỏi băm, 3 muỗng canh nước mắm, 2 muỗng canh đường, 1/3 muỗng canh hạt nêm và 1 muỗng cà phê tiêu, trộn đều.";
                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Bắc nồi lên bếp, cho vào nồi 1 muỗng cà phê đường đun với lửa vừa, cùng lúc đó dùng đũa khuấy đều đến khi nước đường trở màu nâu cánh gián thì tắt bếp.";

                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Bạn gắp thịt đã ướp cho vào nồi nước màu vừa thắng, đảo với lửa lớn cho đến khi thịt săn lại thì đổ 400ml nước dừa vào.";
                }});
                add(new Directions() {{
                    Id = 5;
                    Name = "Đậy nắp nồi lại rồi kho thịt với lửa nhỏ trong vòng 30 phút. Sau 30 phút, nếu thấy nước cạn quá thì cho thêm nước, cho trứng vịt luộc đã bóc vỏ vào nồi, đậy nắp và đun tiếp trong 30 phút cho thịt chín mềm rồi tắt bếp.";

                }});

            }};
            View = 0;
            Love = 0;
            IsConfirm = false;
            Category = getListCategory().get(7);
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/2021/01/CookRecipe/GalleryStep/thanh-pham-268.jpg");
                add("https://cdn.tgdd.vn/2021/01/CookRecipe/GalleryStep/thanh-pham-269.jpg");
                add("https://cdn.tgdd.vn/2021/01/CookRecipe/GalleryStep/thanh-pham-270.jpg");
                add("https://cdn.tgdd.vn/2021/01/CookRecipe/GalleryStep/thanh-pham-271.jpg");


            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_005";
            Name = "Cải ngọt xào tỏi";
            Description = "Rau xào là món ăn ưa chuộng trong những bữa cơm gia đình bởi hương vị thơm ngon, bổ dưỡng lại còn dễ thực hiện.";

            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/2021/04/CookProduct/MonngonCachlamraucaingotxaotoithonngonthanhdamchocothe-KimiFoodTV6-3screenshot-1200x676.jpg";
            RecipeAuth = new RecipeAuth() {{
                AuthId = "user_id_test_member_5";
                AuthName = "Jame";
                Gender = "Male";
                Address = "Viet Nam";
                NickName = "#" + AuthName.toLowerCase();
                Image = "https://img.freepik.com/premium-vector/chef-icon-illustraton-vector_57048-38.jpg?w=740";
            }};
            PrepareTime = new PrepareTime() {{
                Time = "10";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "10";
                TimeType = "m";
            }};
            Level =  "Easy";
            Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                add(new Ingredients() {{
                    Id = 1;
                    Name = "Cải ngọt";
                    Quantitative = 500;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Gừng";
                    Quantitative = 25;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Dầu ăn";
                    Quantitative = 40;
                }});

                add(new Ingredients() {{
                    Id = 4;
                    Name = "Muối, dầu hào, xì dầu (nước tương), đường, hạt nêm, hạt tiêu, tỏi";
                    Quantitative = 15;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "Cải ngọt bạn nhặt bỏ những cọng bị héo, úa rồi đem đi rửa sạch, sau đó cắt khúc dài khoảng 1/2 gang tay. Tỏi bóc vỏ, gừng cạo vỏ, sau đó đập dập và cắt mỏng.";

                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Bắc chảo lên bếp, cho vào chảo 2 muỗng canh dầu ăn và phi thơm tỏi lên. Khi tỏi bắt đầu vàng thì bạn cho gừng vào.";

                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Khi phần tỏi gừng đã thơm thì bạn cho cải ngọt vào xào. Xào được 2 phút thì nêm vào 1 muỗng cà phê muối, 1 muỗng cà phê hạt nêm, 1/3 muỗng cà phê bột ngọt.";

                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Sau đó bạn tiếp tục xào và đảo đều đến khi cải ngọt chín, nêm nếm lại cho vừa ăn. Cho cải ngọt ra dĩa, rắc thêm một ít tiêu để món ăn thêm phần hấp dẫn.";
                }});

            }};
            View = 0;
            Love = 0;
            IsConfirm = false;
            Category = getListCategory().get(7);
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/2021/04/CookRecipe/GalleryStep/1-3.jpg");
                add("https://cdn.tgdd.vn/2022/01/CookRecipe/Avatar/cai-ngot-xao-toi-cong-thuc-duoc-chia-se-tu-nguoi-dung-thumbnail.jpg");


            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_006";
            Name = "Canh xương hầm rau củ";
            Description = "Món canh xương hầm rau củ vừa thơm ngon hấp dẫn vừa cung cấp được những chất dinh dưỡng cho các thành viên trong gia đình. Cùng tham khảo bài viết để biết cách chế biến món ăn hấp dẫn này nhé.";

            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/Files/2019/08/21/1190109/cach-nau-canh-xuong-ham-rau-cu-ngot-nuoc-an-ai-cung-gat-gu-khen-ngon-201908211520033197.jpg";
            RecipeAuth = new RecipeAuth() {{
                AuthId = "user_id_test_member_6";
                AuthName = "Jame";
                Gender = "Male";
                Address = "Viet Nam";
                NickName = "#" + AuthName.toLowerCase();
                Image = "https://img.freepik.com/premium-vector/chef-icon-illustraton-vector_57048-38.jpg?w=740";
            }};
            PrepareTime = new PrepareTime() {{
                Time = "30";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "40";
                TimeType = "m";
            }};
            Level =  "Easy";
            Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                add(new Ingredients() {{
                    Id = 1;
                    Name = "Xương heo";
                    Quantitative = 500;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Củ dền";
                    Quantitative = 150;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Su su";
                    Quantitative = 200;
                }});
                add(new Ingredients() {{
                    Id = 4;
                    Name = "Cà rốt";
                    Quantitative = 150;
                }});
                add(new Ingredients() {{
                    Id = 5;
                    Name = "Khoai tây";
                    Quantitative = 150;
                }});
                add(new Ingredients() {{
                    Id = 6;
                    Name = "Muối, dầu hào, xì dầu (nước tương), đường, hạt nêm, hạt tiêu, tỏi";
                    Quantitative = 15;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "Xương heo bạn có thể nhờ người bán chặt sẵn thành miếng vừa ăn. Về nhà bạn chỉ cần trụng sơ qua với nước sôi để sạch bẩn và mùi hôi. Các loại củ gọt vỏ, cắt miếng vừa ăn rồi rửa sạch.";

                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Cho xương vào nồi cùng một ít bột nêm, đổ thêm nước vào đun đến khi sôi thì vớt bọt trên mặt nước bỏ.";

                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Tiếp đến, hạ nhỏ lửa để hầm ít nhất 45 phút cho xương mềm và nước canh thêm ngọt.";

                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Khi xương đã mềm, cho các loại củ vào nấu thêm 20 phút đến khi củ chín.";
                }});
                add(new Directions() {{
                    Id = 5;
                    Name = "Nêm nếm lại cho hợp khẩu vị, thêm hành ngò vào rồi tắt bếp.";
                }});

            }};
            View = 0;
            Love = 0;
            IsConfirm = false;
            Category = getListCategory().get(7);
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/2021/04/CookRecipe/GalleryStep/1-3.jpg");
                add("https://cdn.tgdd.vn/Files/2019/08/21/1190109/cach-nau-canh-xuong-ham-rau-cu-ngot-nuoc-an-ai-cung-gat-gu-khen-ngon-201908211519024049.jpg");


            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_007";
            Name = "Miến gà";
            Description = "Miến gà là món ăn lý tưởng cho bữa sáng đầy năng lượng. Đây là món ăn ngon, nhiều dinh dưỡng lại không tốn nhiều thời gian. Cách nấu miến gà cũng không quá cầu kỳ, phức tạp, chỉ cần tham khảo bài viết dưới đây, bạn đã có thể làm ngay bát miến gà thơm ngon hết sảy.";

            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/Files/2019/08/21/1190109/cach-nau-canh-xuong-ham-rau-cu-ngot-nuoc-an-ai-cung-gat-gu-khen-ngon-201908211520033197.jpg";
            RecipeAuth = new RecipeAuth() {{
                AuthId = "user_id_test_member_7";
                AuthName = "Jame";
                Gender = "Male";
                Address = "Viet Nam";
                NickName = "#" + AuthName.toLowerCase();
                Image = "https://img.freepik.com/premium-vector/chef-icon-illustraton-vector_57048-38.jpg?w=740";
            }};
            PrepareTime = new PrepareTime() {{
                Time = "15";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "60";
                TimeType = "m";
            }};
            Level =  "Easy";
            Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                add(new Ingredients() {{
                    Id = 1;
                    Name = "Gà";
                    Quantitative = 500;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Lòng mề";
                    Quantitative = 200;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Miến";
                    Quantitative = 200;
                }});
                add(new Ingredients() {{
                    Id = 4;
                    Name = "Hành tây";
                    Quantitative = 100;
                }});
                add(new Ingredients() {{
                    Id = 5;
                    Name = "Nấm mèo";
                    Quantitative = 15;
                }});
                add(new Ingredients() {{
                    Id = 6;
                    Name = "Muối, dầu hào, xì dầu (nước tương), đường, hạt nêm, hạt tiêu, hành, tỏi";
                    Quantitative = 15;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "Thịt gà, mề gà các bạn rửa thật sạch với nước. Hành tây các thái lát mỏng. Giá rửa sạch. Hành lá, ngò rí cắt nhỏ. Nấm mèo xắt sợi. Nướng 4 - 5 củ hành tím sơ qua trên vỉ rồi bóc vỏ";

                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Cho thịt gà và mề gà vào một cái tô lớn, cho vào 1 muỗng cafe hạt nêm, 1 ít tiêu, trộn đều và ướp trong khoảng 10 phút.";

                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Bắc nồi lên bếp với 1,5 lít nước, khi nước sôi thì cho hành tím đã nướng vào. Cho phần thịt gà vào và hầm trong 30 phút.";

                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Khi gà đã hầm trong 30 phút thì cho phần nấm mèo xắt sợi vào, cho thêm 2 muỗng hạt nêm và 1 muỗng muối. Trộn đều để tan các gia vị vừa cho vào. Thử lấy một cây đũa để đâm vào miếng thịt gà, nếu đũa xuyên qua được thịt thì đã chín.";
                }});
                add(new Directions() {{
                    Id = 5;
                    Name = "Khi ăn thì các bạn trụng miến qua nước dùng rồi cho ra tô, thêm lên mặt ít miếng thịt gà xé sợi, hành tây và một ít nấm mèo, sau đó cho nước dùng lên trên, trang trí hành phi, ngò và hành lá lên bề mặt là đã hoàn thành rồi.";
                }});

            }};
            View = 0;
            Love = 0;
            IsConfirm = false;
            Category = getListCategory().get(7);
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/Files/2017/03/22/963778/bi-quyet-nau-mien-ga-thom-ngon-da-ga-vang-gion-202208261323269332.jpg");
                add("https://cdn.tgdd.vn/Files/2017/03/22/963778/bi-quyet-nau-mien-ga-thom-ngon-da-ga-vang-gion-202203151059564429.jpg");


            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_008";
            Name = "Trà sữa thái xanh";
            Description = "Trà sữa Thái đã dần trở thành một loại thức uống giải khát quen thuộc được nhiều người yêu thích. Món thức uống hấp dẫn này không chỉ có sức hút với các bạn tuổi teen mà nó còn khiến nhiều người thuộc lứa tuổi khác nhau mê mẩn. Không những có mùi vị lạ miệng, trà sữa Thái còn sở hữu màu sắc đẹp, bắt mắt.";

            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/Files/2019/08/21/1190109/cach-nau-canh-xuong-ham-rau-cu-ngot-nuoc-an-ai-cung-gat-gu-khen-ngon-201908211520033197.jpg";
            RecipeAuth = new RecipeAuth() {{
                AuthId = "user_id_test_member_8";
                AuthName = "Jame";
                Gender = "Male";
                Address = "Viet Nam";
                NickName = "#" + AuthName.toLowerCase();
                Image = "https://img.freepik.com/premium-vector/chef-icon-illustraton-vector_57048-38.jpg?w=740";
            }};
            PrepareTime = new PrepareTime() {{
                Time = "5";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "25";
                TimeType = "m";
            }};
            Level =  "Easy";
            Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                add(new Ingredients() {{
                    Id = 1;
                    Name = "Trà thái xanh";
                    Quantitative = 30;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Đường";
                    Quantitative = 150;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Sữa đặc";
                    Quantitative = 200;
                }});
                add(new Ingredients() {{
                    Id = 4;
                    Name = "Sữa tươi có đường";
                    Quantitative = 220;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "Bạn bắc nồi lên bếp, cho vào 2.5 lít nước sau đó đun sôi. Có thể gia giảm lượng nước tùy theo số lượng ly trà bạn nấu nhé. Khi nước đã sôi bạn tắt bếp, cho toàn bộ phần trà vào. Đậy nắp và ủ trà trong khoảng 10 phút.";

                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Sau khi ủ đủ thời gian bạn dùng rây, lọc phần xát trà, chỉ giữ lại nước trà nguyên chất. Nhớ rây thật kỹ để tránh uống phải phần bã trà nhé.";

                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Trà lọc sạch bã, bạn cho lại lên bếp. Lưu ý lúc này đun với lửa thật nhỏ không cho trà nổi bọt. Cho đường vào khuấy tan sau đó cho tiếp sữa đặc và sau cùng là sữa tươi. Khuấy đều hỗn hợp khoảng 2 phút rồi tắt bếp.";

                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Bạn đợi nguội, cho đá vào ly, rót phần trà sữa đã nấu vào là đã có ngay một ly trà sữa thái xanh thơm ngon siêu cấp.";

                }});

            }};
            View = 0;
            Love = 0;
            IsConfirm = false;
            Category = getListCategory().get(7);
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/Files/2019/11/11/1217662/cach-lam-tra-sua-thai-voi-bot-beo-don-gian-vua-ngon-vua-thom-mat-cho-nang-sanh-an-vat-202103111454181920.jpg");
                add("https://cdn.tgdd.vn/Files/2019/11/11/1217662/4-cach-nau-tra-sua-thai-xanh-va-tra-sua-thai-do-ngon-tai-nha-202203180949391758.jpg");
                add("https://cdn.tgdd.vn/Files/2019/11/11/1217662/4-cach-nau-tra-sua-thai-xanh-va-tra-sua-thai-do-ngon-tai-nha-202203180957391520.jpg");


            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_009";
            Name = "Trà sữa thái đỏ";
            Description = "Trà sữa Thái đã dần trở thành một loại thức uống giải khát quen thuộc được nhiều người yêu thích. Món thức uống hấp dẫn này không chỉ có sức hút với các bạn tuổi teen mà nó còn khiến nhiều người thuộc lứa tuổi khác nhau mê mẩn. Không những có mùi vị lạ miệng, trà sữa Thái còn sở hữu màu sắc đẹp, bắt mắt.";

            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/Files/2019/11/11/1217662/4-cach-nau-tra-sua-thai-xanh-va-tra-sua-thai-do-ngon-tai-nha-202203181036130891.jpg";
            RecipeAuth = new RecipeAuth() {{
                AuthId = "user_id_test_member_9";
                AuthName = "Jame";
                Gender = "Male";
                Address = "Viet Nam";
                NickName = "#" + AuthName.toLowerCase();
                Image = "https://img.freepik.com/premium-vector/chef-icon-illustraton-vector_57048-38.jpg?w=740";
            }};
            PrepareTime = new PrepareTime() {{
                Time = "5";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "30";
                TimeType = "m";
            }};
            Level =  "Difficult";
            Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                add(new Ingredients() {{
                    Id = 1;
                    Name = "Trà thái đỏ";
                    Quantitative = 30;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Nước nóng";
                    Quantitative = 180;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Sữa đặc";
                    Quantitative = 30;
                }});
                add(new Ingredients() {{
                    Id = 4;
                    Name = "Kem lỏng";
                    Quantitative = 30;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "Đầu tiên, bạn cho vào bình 2 muỗng canh trà thái đỏ, 180ml nước nóng để ủ trà trong vòng 20 phút. ";

                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Sau khi đủ thời gian thì bạn cho trà vào một túi vải để lọc bã trà, thu được khoảng 150ml nước trà thái đỏ.";

                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Tiếp theo, bạn cho trà vào ly cùng với 30ml sữa đặc, 30ml kem lỏng và khuấy đều.";
                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Cuối cùng, bạn cho đá viên vào ly, cho trà sữa thái đỏ đã pha vào và thưởng thức.";

                }});

            }};
            View = 0;
            Love = 0;
            IsConfirm = false;
            Category = getListCategory().get(3);
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/Files/2019/11/11/1217662/4-cach-nau-tra-sua-thai-xanh-va-tra-sua-thai-do-ngon-tai-nha-202203181033463219.jpg");
                add("https://cdn.tgdd.vn/Files/2019/11/11/1217662/4-cach-nau-tra-sua-thai-xanh-va-tra-sua-thai-do-ngon-tai-nha-202203181002366801.jpg");
                add("https://cdn.tgdd.vn/Files/2019/11/11/1217662/4-cach-nau-tra-sua-thai-xanh-va-tra-sua-thai-do-ngon-tai-nha-202203181036130891.jpg");


            }};
        }});

        return listData;
    }


}
