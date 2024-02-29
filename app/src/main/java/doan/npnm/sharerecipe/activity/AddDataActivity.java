package doan.npnm.sharerecipe.activity;

import android.net.Uri;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

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

        binding.btnAddRecipe.setOnClickListener(v -> getRecipe().forEach(recipe -> {

            firestore.collection(Constant.RECIPE)
                    .document(recipe.Id)
                    .set(recipe)
                    .addOnSuccessListener(task -> {
                        onResult("Success");
                    }).addOnFailureListener(e -> {
                        onResult(e.getMessage());
                    });

        }));

        binding.btnAddCategory.setOnClickListener(v -> getListCategory());
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


    public void getListCategory() {
        ArrayList<Integer> arrDrawable = new ArrayList<Integer>() {{
            add(R.drawable.category_bakery);
            add(R.drawable.category_beverages);
            add(R.drawable.category_dairy);
            add(R.drawable.category_frozen);
            add(R.drawable.category_fruit);
            add(R.drawable.category_meat);
            add(R.drawable.category_poultry);
            add(R.drawable.category_seafood);
            add(R.drawable.category_vegettable);
        }};

        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category("1", AppContext.getContext().getString(R.string.bakery), ""));
        categories.add(new Category("2", AppContext.getContext().getString(R.string.beverages), ""));
        categories.add(new Category("3", AppContext.getContext().getString(R.string.dairy), ""));
        categories.add(new Category("4", AppContext.getContext().getString(R.string.frozen), ""));
        categories.add(new Category("5", AppContext.getContext().getString(R.string.fruit), ""));
        categories.add(new Category("6", AppContext.getContext().getString(R.string.meat), ""));
        categories.add(new Category("7", AppContext.getContext().getString(R.string.poultry), ""));
        categories.add(new Category("8", AppContext.getContext().getString(R.string.seafood), ""));
        categories.add(new Category("9", AppContext.getContext().getString(R.string.vegetable), ""));

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        for (int i = 0; i < arrDrawable.size(); i++) {
            int drawableResId = arrDrawable.get(i);
            String imageName = "category_image_" + i + ".png";
            StorageReference imageRef = storageRef.child(Constant.CATEGORY + "/" + imageName);

            final int index = i; // Capture the value of i locally

            UploadTask uploadTask = imageRef.putFile(Uri.parse("android.resource://doan.npnm.sharerecipe/" + drawableResId));
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imageRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    categories.get(index).Image = downloadUri.toString();
                    putCategory(categories.get(index));
                }
            });
        }
    }

    private void putCategory(Category category) {
        firestore.collection(Constant.CATEGORY)
                .document(category.Id)
                .set(category)
                .addOnSuccessListener(task -> {
                    onResult("Success");
                }).addOnFailureListener(e -> {
                    onResult(e.getMessage());
                });
    }


    public ArrayList<String> getCategoryID() {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            result.add("" + i);
        }
        return result;
    }


    public ArrayList<Recipe> getRecipe() {


        ArrayList<Recipe> listData = new ArrayList<>();
        listData.add(
                new Recipe() {{
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
                        add("Add Time=" + getTimeNow());
                    }};

                    Love = 0;
                    RecipeStatus = RecipeStatus.PREVIEW;
                    Category = getCategoryID();
                    ImagePreview = new ArrayList<String>() {{
                        add("https://i1-ngoisao.vnecdn.net/2013/09/10/ca3-8382-1378805776.jpg?w=0&h=0&q=100&dpr=1&fit=crop&s=aaKX_AyHxICsPmtJR5s4tg");
                        add("https://i1-ngoisao.vnecdn.net/2013/09/10/ca4-7404-1378805777.jpg?w=0&h=0&q=100&dpr=1&fit=crop&s=hFJeJJPYJZoqNS-xDW4peA");
                        add("https://i1-ngoisao.vnecdn.net/2013/09/10/ca5-6464-1378805777.jpg?w=0&h=0&q=100&dpr=1&fit=crop&s=9dZNg8dpvJ3Ng-AhUHWqEQ");
                        add("https://i1-ngoisao.vnecdn.net/2013/09/10/ca7-5617-1378805777.jpg?w=0&h=0&q=100&dpr=1&fit=crop&s=Mo2rH8pttH6tMPWt9AoycQ");
                    }};
                }});
        listData.add(new Recipe() {{
            Id = "Recipe_002";
            Name = "Mực ống xào với dứa và hành tây";
            Description = "Mực là một loại hải sản quen thuộc, giàu dinh dưỡng và có thể chế biến thành rất nhiều món ăn ngon, nhất là món xào.";
            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/2021/08/CookRecipe/Avatar/muc-xao-dua-thom-thumbnail-1.jpg";
            RecipeAuth = "user_id_test_member2";
            PrepareTime = new PrepareTime() {{
                Time = "15";
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
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
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
            RecipeAuth = "user_id_test_member3";
            PrepareTime = new PrepareTime() {{
                Time = "15";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "20";
                TimeType = "m";
            }};
            Level = "Easy";
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
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
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
            RecipeAuth = "user_id_test_member4";
            PrepareTime = new PrepareTime() {{
                Time = "60";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "45";
                TimeType = "m";
            }};
            Level = "Difficult";
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
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
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
            RecipeAuth = "user_id_test_member5";
            PrepareTime = new PrepareTime() {{
                Time = "10";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "10";
                TimeType = "m";
            }};
            Level = "Difficult";
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
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/2021/04/CookRecipe/GalleryStep/1-3.jpg");
                add("https://cdn.tgdd.vn/2022/01/CookRecipe/Avatar/cai-ngot-xao-toi-cong-thuc-duoc-chia-se-tu-nguoi-dung-thumbnail.jpg");


            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_006";
            Name = "Canh xương hầm rau củ";
            Description = "Món canh xương hầm rau củ vừa thơm ngon hấp dẫn vừa cung cấp được những chất dinh dưỡng cho các thành viên trong gia đình." +
                    " Cùng tham khảo bài viết để biết cách chế biến món ăn hấp dẫn này nhé.";

            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/Files/2019/08/21/1190109/cach-nau-canh-xuong-ham-rau-cu-ngot-nuoc-an-ai-cung-gat-gu-khen-ngon-201908211520033197.jpg";
            RecipeAuth = "user_id_test_member6";
            PrepareTime = new PrepareTime() {{
                Time = "30";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "40";
                TimeType = "m";
            }};
            Level = "Difficult";
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
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
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
            RecipeAuth = "user_id_test_member7";
            PrepareTime = new PrepareTime() {{
                Time = "15";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "60";
                TimeType = "m";
            }};
            Level = "Difficult";
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
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
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
            RecipeAuth = "user_id_test_member8";
            PrepareTime = new PrepareTime() {{
                Time = "5";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "25";
                TimeType = "m";
            }};
            Level = "Difficult";
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
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
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
            RecipeAuth = "user_id_test_member9";
            PrepareTime = new PrepareTime() {{
                Time = "5";
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
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/Files/2019/11/11/1217662/4-cach-nau-tra-sua-thai-xanh-va-tra-sua-thai-do-ngon-tai-nha-202203181033463219.jpg");
                add("https://cdn.tgdd.vn/Files/2019/11/11/1217662/4-cach-nau-tra-sua-thai-xanh-va-tra-sua-thai-do-ngon-tai-nha-202203181002366801.jpg");
                add("https://cdn.tgdd.vn/Files/2019/11/11/1217662/4-cach-nau-tra-sua-thai-xanh-va-tra-sua-thai-do-ngon-tai-nha-202203181036130891.jpg");


            }};
        }});
        listData.add(new Recipe() {
            {
                Id = "Recipe_010";
                Name = "Bò sốt vang";
                Description = "Bò sốt vang là một trong những món từ bò vô cùng thơm ngon. Nếu bạn đã thử thưởng thức bò số vang hương vị miền Nam thì đổi vị với bò sốt vang miền Bắc, đảm bảo thơm ngon chuẩn vị. ";

                TimeInit = "" + System.currentTimeMillis();
                ImgUrl = "https://cdn.tgdd.vn/2021/09/CookDish/cach-nau-bo-sot-vang-mien-bac-thom-lung-ngon-dung-dieu-avt-1200x676.jpg";
                RecipeAuth = "user_id_test_member9";
                PrepareTime = new PrepareTime() {{
                    Time = "15";
                    TimeType = "m";
                }};
                CookTime = new CookTime() {{
                    Time = "35";
                    TimeType = "m";
                }};
                Level = "Normal";
                Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                    add(new Ingredients() {{
                        Id = 1;
                        Name = "Nạm thịt bò";
                        Quantitative = 500;
                    }});
                    add(new Ingredients() {{
                        Id = 2;
                        Name = "Rượu vang đỏ";
                        Quantitative = 60;
                    }});
                    add(new Ingredients() {{
                        Id = 3;
                        Name = "Cà chua";
                        Quantitative = 60;
                    }});
                    add(new Ingredients() {{
                        Id = 4;
                        Name = "Cà rốt";
                        Quantitative = 70;
                    }});
                    add(new Ingredients() {{
                        Id = 5;
                        Name = "Khoai tây";
                        Quantitative = 70;
                    }});
                    add(new Ingredients() {{
                        Id = 6;
                        Name = "Hành tây";
                        Quantitative = 10;
                    }});
                    add(new Ingredients() {{
                        Id = 7;
                        Name = "Gừng";
                        Quantitative = 10;
                    }});
                    add(new Ingredients() {{
                        Id = 8;
                        Name = "Bột năng";
                        Quantitative = 20;
                    }});
                    add(new Ingredients() {{
                        Id = 9;
                        Name = "Bơ lạt";
                        Quantitative = 30;
                    }});
                    add(new Ingredients() {{
                        Id = 10;
                        Name = "Tương cà";
                        Quantitative = 20;
                    }});
                    add(new Ingredients() {{
                        Id = 11;
                        Name = "Gói sốt bò kho ";
                        Quantitative = 30;
                    }});

                }};
                Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                    add(new Directions() {{
                        Id = 1;
                        Name = "Cà chua bạn rửa sạch với nước sau đó cắt cà chua thành các múi cau nhỏ. Sau đó bạn dùng dao lọc bỏ phần ruột cà chua đi để tránh cho món ăn của mình bị chua quá nhé.";

                    }});
                    add(new Directions() {{
                        Id = 2;
                        Name = "Sau khi lọc bỏ hạt cà chua, bạn cho cà chua và máy xay rồi xay nhuyễn. Nếu không có máy xay bạn có thể trụng cà chua khoảng 3 - 5 phút cho cà mềm rồi dằm nhuyễn nhé.";

                    }});
                    add(new Directions() {{
                        Id = 3;
                        Name = "Cà rốt và khoai tây bạn rửa sạch rồi gọt bỏ vỏ. Sau đó bạn cắt thành từng cục vừa ăn. Đừng cắt quá to, khi hầm sẽ lâu chín, ngược lại đừng cắt quá nhỏ khi hầm dễ bị nát nhé.";
                    }});
                    add(new Directions() {{
                        Id = 4;
                        Name = "Đặt chảo lên bếp, cho vào chảo 15gr bơ lạt rồi bật lửa vừa. Khi bơ chảy hoàn toàn thì bạn cho hành tây đã cắt nhỏ vào phi cho thơm vàng.";

                    }});
                    add(new Directions() {{
                        Id = 5;
                        Name = "Tiếp đến bạn cho phần sốt cà chua đã xay nhuyễn vào chảo. Đun với lửa vừa khoảng 3 phút và khuấy đều tay. Khi hỗn hợp nóng sôi nhẹ thì bạn tắt bếp.";
                    }});
                    add(new Directions() {{
                        Id = 6;
                        Name = "Bạn rửa sạch thịt bò với nước, sau đó bạn cắt thịt thành từng cục hình vuông, mỗi cục dài khoảng 1 lóng tay.";

                    }});
                    add(new Directions() {{
                        Id = 7;
                        Name = "Sau đó bạn bắc 1 nồi nước lên bếp, cho vào nồi thêm phần gừng cắt lát đã chuẩn bị. Khi nước bắt đầu nóng, bọt li ti ở đáy nồi thì bạn cho thịt bò vào trụng trong khoảng 1 phút để giúp khử hôi thịt bò.";

                    }});
                    add(new Directions() {{
                        Id = 8;
                        Name = "Sau khi trụng phần thịt xong, bạn vớt thịt bò cho vào tô. Sau đó bạn thêm 1 gói sốt bò kho (khoảng 50gr) cùng 10ml rượu vang đỏ. Tiếp đến bạn trộn đều rồi ướp thịt bò trong trong 30 phút cho thịt ngấm đều gia vị.";

                    }});
                    add(new Directions() {{
                        Id = 9;
                        Name = "Cho vào nồi 15gr bơ lạt còn lại, sau đó bật bếp đun cho bơ chảy ra hoàn toàn. Sau đó bạn cho phần tỏi băm vào nồi. Phi tỏi cho thơm vàng rồi cho tiếp phần bò đã ướp vào.";

                    }});
                    add(new Directions() {{
                        Id = 10;
                        Name = "Xào thịt bò với lửa vừa trong khoảng 7 phút đến khi thịt bò săn lại thì bạn cho 1 tô nước đầy vào nồi sao cho nước ngập mặt thịt bò nhé.";

                    }});
                    add(new Directions() {{
                        Id = 11;
                        Name = "Sau khoảng 10 - 15 phút, nồi thịt bò sôi lên thì bạn cho phần sốt cà chua đã đun ở trên vào nồi. Tiếp tục hầm thịt bò ở lửa nhỏ vừa trong 60 phút cho thịt bò mềm.";

                    }});
                    add(new Directions() {{
                        Id = 12;
                        Name = "Sau 60 phút hầm, bạn cho tiếp cà rốt vào hầm tiếp 15 phút. Sau 15 phút bạn cho khoai tây vào hầm đến khi khoai tây mềm hẳn. Cà rốt sẽ lâu mềm hơn khoai tây nên bạn cho cà rốt vào hầm trước nhé.";

                    }});
                    add(new Directions() {{
                        Id = 13;
                        Name = "Tiếp đến bạn nêm vào nồi 15gr tương cà, 50ml rượu vang đỏ còn lại rồi khuấy đều một lần nữa. Đậy nắp và đun đến khi nước sôi trở lại thì bạn tắt bếp. Nêm nếm lại cho vừa khẩu vị rồi múc ra tô thưởng thức ngay nào";

                    }});
                }};
                View = 0;
                Share = 0;
                History = new ArrayList<String>() {{
                    add("Add Time=" + getTimeNow());
                }};
                Love = 0;
                RecipeStatus = RecipeStatus.PREVIEW;
                Category = getCategoryID();
                ImagePreview = new ArrayList<String>() {{
                    add("https://cdn.tgdd.vn/2021/09/CookRecipe/GalleryStep/thanh-pham-1092.jpg");
                    add("https://cdn.tgdd.vn/2021/09/CookRecipe/GalleryStep/thanh-pham-1093.jpg");
                    add("https://cdn.tgdd.vn/2021/09/CookDish/cach-nau-bo-sot-vang-mien-bac-thom-lung-ngon-dung-dieu-avt-1200x676.jpg");
                }};
            }
        });
        listData.add(new Recipe() {{
            Id = "Recipe_012";
            Name = "Sườn xào chua ngọt";
            Description = "Bữa ăn gia đình sẽ phong phú hơn với các món xào hấp dẫn, thơm ngon, Mình sẽ mách bạn bí quyết thực hiện 2 cách làm món sườn xào chua ngọt cực đậm đà, bắt cơm với công thức siêu đơn giản.";
            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/2020/12/CookRecipe/GalleryStep/thanh-pham-1107.jpg";
            RecipeAuth = "user_id_test_member12";
            PrepareTime = new PrepareTime() {{
                Time = "20";
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
                    Name = "Sườn heo non";
                    Quantitative = 500;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Ớt chuông";
                    Quantitative = 100;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Hành tây nhỏ";
                    Quantitative = 40;
                }});
                add(new Ingredients() {{
                    Id = 4;
                    Name = "Giấm";
                    Quantitative = 15;
                }});
                add(new Ingredients() {{
                    Id = 5;
                    Name = "Bột năng";
                    Quantitative = 30;
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
                    Name = "Để sườn non sạch và hết mùi hôi bạn rửa sạch với nước, sau đó bắc 1 cái nồi lên bếp cho vào 1 chút muối. Khi nước sôi, cho sườn vào chần sơ khoảng 3 - 5 phút.";

                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Vớt sườn non ra tô cho ráo nước, sau đó ướp vào sườn với 2 muỗng cà phê nước mắm, 1 muỗng cà phê đường, 1 muỗng cà phê hạt nêm trong khoảng 15 phút.";

                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Sau khi sườn thấm gia vị, bạn lăn sườn qua một lớp bột năng. Tiếp theo, cho dầu vào một chiếc chảo và bắc lên bếp. Khi dầu nóng, cho sườn vào chiên tới khi vàng đều, sau đó gắp ra dĩa có lót giấy thấm dầu.";

                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Cắt nhỏ ớt chuông và hành tây thành miếng vừa ăn. Tiếp theo pha sốt chua ngọt gồm: 3 muỗng canh giấm, 1 muỗng canh đường, 2 muỗng canh nước mắm, 2 tép tỏi băm, 1 trái ớt băm (khẩu vị có thể tăng giảm theo ý thích của bạn nhé), sau đó khuấy đều.";

                }});
                add(new Directions() {{
                    Id = 5;
                    Name = "Bắc 1 cái chảo khác lên bếp, cho vào một ít dầu và cho 1 ít hành tím băm vào xào cho thơm. Tiếp đến, cho ớt chuông vào xào.";
                }});
                add(new Directions() {{
                    Id = 6;
                    Name = "Rồi cho sườn vào xào chung. Khi ớt chuông sắp chín, bạn cho hành tây và sốt chua ngọt vào xào. Cuối cùng hòa 1 muỗng cà phê bột năng vào một tí nước rồi cho vào sườn xào tới khi sệt lại là đạt.";

                }});

            }};
            View = 0;
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/2020/12/CookRecipe/GalleryStep/thanh-pham-1107.jpg");
                add("https://cdn.tgdd.vn/2020/12/CookRecipe/GalleryStep/xao-suon-chua-ngot-1.jpg");
                add("https://cdn.tgdd.vn/2020/12/CookRecipe/GalleryStep/xao-suon-chua-ngot.jpg");

            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_013";
            Name = "Lẩu ếch lá giang";
            Description = "Ngày mưa mà được ngồi nhâm nhi bên nồi lẩu ếch cùng bạn bè, gia đình thì còn gì tuyệt hơn đúng không nào.";
            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/2022/10/CookDish/2-cach-nau-lau-ech-voi-la-giang-va-com-me-thom-ngon-ngay-mua-avt-1200x676.jpg";
            RecipeAuth = "user_id_test_member13";
            PrepareTime = new PrepareTime() {{
                Time = "20";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "25";
                TimeType = "m";
            }};
            Level = "Difficult";
            Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                add(new Ingredients() {{
                    Id = 1;
                    Name = "Sườn heo non";
                    Quantitative = 500;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Ớt chuông";
                    Quantitative = 100;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Hành tây nhỏ";
                    Quantitative = 40;
                }});
                add(new Ingredients() {{
                    Id = 4;
                    Name = "Giấm";
                    Quantitative = 15;
                }});
                add(new Ingredients() {{
                    Id = 5;
                    Name = "Bột năng";
                    Quantitative = 30;
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
                    Name = "Đầu tiên, bạn đem ếch đi làm sạch rồi rửa với muối, giấm để giảm bớt mùi tanh.";

                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Kế đến, để thịt ếch ráo nước rồi ướp cùng 1 muỗng cà phê bột ngọt, 1 muỗng canh nước mắm, 1 muỗng cà phê đường trong vòng 15 phút cho thấm đều gia vị.";

                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Với nấm rơm, bạn dùng dao gọt bỏ phần chân nấm dơ rồi ngâm nước muối trong khoảng 10 - 15 phút, sau đó rửa sạch.";
                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Lá giang mua về nhặt bỏ lá hư, rửa sạch với nước muối loãng, sau đó rửa lại với nước sạch.";

                }});
                add(new Directions() {{
                    Id = 5;
                    Name = "Tiếp đến, bạn đem rau chuối bào, rau nhút đã nhặt lấy cọng non rửa với nước sạch, rồi để ráo.";

                }});
                add(new Directions() {{
                    Id = 6;
                    Name = "Bạn bắc chảo lên bếp, đợi dầu nóng rồi cho ếch vào xào với lửa nhỏ đến khi thịt săn lại thì tắt bếp";

                }});
                add(new Directions() {{
                    Id = 7;
                    Name = "Trước tiên, bạn cho 2 muỗng cà phê tỏi băm vào nồi phi thơm, sau đó cho lần lượt cà chua, măng chua, nấm rơm đã sơ chế vào xào khoảng 5 phút rồi đổ 1.5 lít nước vào nấu với lửa vừa.";

                }});
                add(new Directions() {{
                    Id = 8;
                    Name = "Khi thấy nước dùng sôi lên, cho lá giang đã vò vào nấu cho đến khi ra vị chua, bạn cho phần ếch đã xào vào nấu thêm khoảng 10 phút.";

                }});
                add(new Directions() {{
                    Id = 9;
                    Name = "Cuối cùng, bạn nêm nếm lại cho vừa ăn, cho thêm rau dùng kèm rồi tắt bếp. Vậy là món ăn đã hoàn thành.";
                }});


            }};
            View = 0;
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/2022/10/CookRecipe/GalleryStep/thanh-pham-19.jpg");
                add("https://cdn.tgdd.vn/2020/09/CookRecipe/Avatar/lau-ech-com-me-thumbnail.jpg");

            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_014";
            Name = "Nộm hoa chuối tai heo";
            Description = "Bạn đang thắc mắc cách làm nộm hoa chuối tai heo không thâm, giòn và ngon? Mình sẽ hướng dẫn bạn làm món ăn này tại nhà cực ngon nhé!";
            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/Files/2021/08/02/1372370/lam-nom-hoa-chuoi-tai-heo-khong-tham-gion-ngon-doi-vi-cho-bua-an-gia-dinh-202108020032423746.jpg";
            RecipeAuth = "user_id_test_member14";
            PrepareTime = new PrepareTime() {{
                Time = "20";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "20";
                TimeType = "m";
            }};
            Level = "Difficult";
            Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                add(new Ingredients() {{
                    Id = 1;
                    Name = "Tai heo";
                    Quantitative = 500;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Hoa chuối";
                    Quantitative = 400;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Cà rốt";
                    Quantitative = 70;
                }});
                add(new Ingredients() {{
                    Id = 4;
                    Name = "Lạc khô";
                    Quantitative = 100;
                }});
                add(new Ingredients() {{
                    Id = 5;
                    Name = "Giấm";
                    Quantitative = 20;
                }});
                add(new Ingredients() {{
                    Id = 6;
                    Name = "Muối, dầu hào, xì dầu (nước tương),chanh, đường, hạt nêm, hạt tiêu, tỏi";
                    Quantitative = 15;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "Làm sạch tai heo: Tai heo dùng dao cạo sạch phần lông bám trên tai và phần chất bẩn bên trong tai heo. Sau đó lấy muối hòa với chanh chà sạch trên phần tai. Lấy giấm hoặc rượu ngâm tai heo khoảng tầm 10 - 15 phút để loại bỏ hoàn toàn mùi hôi của tai.";

                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Về hoa chuối nếu bạn mua hoa chuối thái sẵn thì chỉ cần bỏ vào ngâm với muối và chanh.";

                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Cà rốt rửa sạch và bào ra từng sợi. Rau thơm các loại, hành tây rửa sạch qua nước muối và thái nhỏ.";
                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Bỏ tai heo vào nồi luộc kèm theo 1 thìa bột canh. Sau khi luộc xong vớt tai heo ra ngâm với nước đá tầm 15 phút để tai giòn và trắng sạch. Sau khi ngâm nước đá xong thì lấy ra để cho khô nước và thái từng sợi nhỏ.";

                }});
                add(new Directions() {{
                    Id = 5;
                    Name = "Đầu tiên bạn cần làm nước trộn nộm theo công thức sau: 3 thìa cà phê mắm, 5 thìa cà phê đường, 5 thìa cà phê nước cốt chanh (có thể thay bằng giấm cũng được nhé), tỏi, ớt băm nhuyễn.";

                }});
                add(new Directions() {{
                    Id = 6;
                    Name = "Sau đó bạn bỏ tất cả các nguyên liệu mà mình đã sơ chế sẵn cho vào nồi và tưới nước sốt đã làm bên trên vào, dùng đũa trộn đều các hỗn hợp với nhau (lưu ý trộn nhẹ nhàng để tránh tình trạng rau thơm bị dập nát).";

                }});
                add(new Directions() {{
                    Id = 7;
                    Name = "Để trong vòng 15 phút cho các nguyên liệu thấm vào nhau, sau đó bày lên đĩa rắc thêm một ít lạc rang, hành phi thơm và ớt thái lát bỏ hạt lên bề mặt trên rồi thưởng thức nhé!";
                }});
            }};
            View = 0;
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/Files/2021/08/02/1372370/lam-nom-hoa-chuoi-tai-heo-khong-tham-gion-ngon-doi-vi-cho-bua-an-gia-dinh-202108020053597667.jpg");
                add("hhttps://cdn.tgdd.vn/Files/2021/08/02/1372370/lam-nom-hoa-chuoi-tai-heo-khong-tham-gion-ngon-doi-vi-cho-bua-an-gia-dinh-202108020032423746.jpg");
                add("https://cdn.tgdd.vn/Files/2021/08/02/1372370/lam-nom-hoa-chuoi-tai-heo-khong-tham-gion-ngon-doi-vi-cho-bua-an-gia-dinh-202108020044365314.jpg");

            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_015";
            Name = "Miến măng vịt";
            Description = "Miến măng vịt là một trong những món nước thơm ngon, cách nấu cũng rất đơn giản với những nguyên liệu dễ tìm như thịt vịt, măng, miến,... Mình sẽ hướng dẫn bạn làm món ăn này tại nhà cực ngon nhé!";
            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/Files/2019/09/08/1196161/huong-dan-cach-lam-mieng-mang-vit-thom-ngon-don-gian-tai-nha-10-760x367.jpg";
            RecipeAuth = "user_id_test_member15";
            PrepareTime = new PrepareTime() {{
                Time = "60";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "5";
                TimeType = "m";
            }};
            Level = "Easy";
            Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                add(new Ingredients() {{
                    Id = 1;
                    Name = "Thịt vịt";
                    Quantitative = 500;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Miến dong";
                    Quantitative = 200;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Măng khô";
                    Quantitative = 150;
                }});
                add(new Ingredients() {{
                    Id = 4;
                    Name = "Rượu trắng";
                    Quantitative = 100;
                }});
                add(new Ingredients() {{
                    Id = 5;
                    Name = "Rau răm";
                    Quantitative = 20;
                }});
                add(new Ingredients() {{
                    Id = 6;
                    Name = "Nấm hương khô, Muối, dầu hào, xì dầu (nước tương),chanh, đường, hạt nêm, hạt tiêu, tỏi";
                    Quantitative = 15;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "Vịt làm sạch, dùng gừng, muối xát xung quanh, sau đó dùng rượu trắng rửa lại để khử sạch mùi hôi, rửa lại bằng nước sạch.";

                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Măng khô rửa sạch, ngâm cho nở rồi cho vào nồi luộc qua, rửa sạch, để ráo. Nấm hương rửa sạch, ngâm nở, thái miếng nhỏ (hoặc để nguyên nấu muốn).";

                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Hành tím lột vỏ, băm nhỏ. Ớt rửa sạch, bỏ cuống, thái lát. Hành lá, rau răm nhặt sạch, rửa sạch, thái nhỏ. Gừng cạo vỏ, băm nhỏ.";
                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Cho vịt vào nồi, đổ ngập nước luộc chín, sau đó vớt vịt ra ngâm vào chậu nước lạnh một lúc, sau đó lấy ra chặt miếng vừa ăn.";

                }});
                add(new Directions() {{
                    Id = 5;
                    Name = "Cho dầu ăn vào nồi đun nóng, trút hành tím vào phi thơm. Sau đó cho măng vào nấm vào đảo chín. Nêm thêm 1/2 muỗng hạt nêm để măng và nấm thêm đậm vị.";

                }});
                add(new Directions() {{
                    Id = 6;
                    Name = "Cho phần măng vừa xào vào nồi nước luộc thịt vịt đun sôi sau đó vặn nhỏ lửa đun cho tới khi măng mềm. Nêm nếm cho vừa ăn.";

                }});
                add(new Directions() {{
                    Id = 7;
                    Name = "Miến ngâm nở, khi gần ăn thả miến vào nồi nước dùng rồi vớt nhanh ra tô, thêm thịt vịt lên trên, chan nước dùng và hành lá, rau thơm, chanh, ớt lên trên. Thịt vịt chấm kèm với nước mắm.";

                }});
            }};
            View = 0;
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/2022/10/CookRecipe/GalleryStep/thanh-pham-49.jpg");
                add("https://cdn.tgdd.vn/2020/07/CookRecipe/Avatar/mieng-mang-vit-thumbnail.jpg");
                add("https://cdn.tgdd.vn/Files/2019/09/08/1196161/huong-dan-cach-lam-mieng-mang-vit-thom-ngon-don-gian-tai-nha.jpg");

            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_016";
            Name = "Ốc nấu chuối đậu";
            Description = "Ốc nấu chuối đậu là món ăn thơm ngon, dân dã, đậm hương vị ẩm thực Bắc Bộ bạn đã thử qua chưa? Nếu chưa thì để mình sẽ hướng dẫn bạn làm món ăn này tại nhà cực ngon nhé!";
            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/Files/2019/09/08/1196161/huong-dan-cach-lam-mieng-mang-vit-thom-ngon-don-gian-tai-nha-10-760x367.jpg";
            RecipeAuth = "user_id_test_member16";
            PrepareTime = new PrepareTime() {{
                Time = "20";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "20";
                TimeType = "m";
            }};
            Level = "Easy";
            Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                add(new Ingredients() {{
                    Id = 1;
                    Name = "Thịt ốc bưu";
                    Quantitative = 300;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Lá tía tô";
                    Quantitative = 100;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Lá lốt";
                    Quantitative = 300;
                }});
                add(new Ingredients() {{
                    Id = 4;
                    Name = "Chuối xanh";
                    Quantitative = 150;
                }});
                add(new Ingredients() {{
                    Id = 5;
                    Name = "Đậu hũ chiên";
                    Quantitative = 400;
                }});
                add(new Ingredients() {{
                    Id = 6;
                    Name = "Mẻ";
                    Quantitative = 100;
                }});
                add(new Ingredients() {{
                    Id = 7;
                    Name = "Nấm hương khô, Muối, dầu hào, xì dầu (nước tương),chanh, đường, hạt nêm, hạt tiêu, Bột bắp, Nghệ, tỏi";
                    Quantitative = 15;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "Ốc mua về để khử mùi hôi và làm rửa sạch, loại bỏ phần ruột ốc. Bóp muối 2 - 3 lần cho sạch nhớt rồi xả lại nước.";

                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Nấu 1 nồi nước sôi cho ốc vào trụng sơ khoảng 2 phút cho ốc thật sạch nhớt.";

                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Nghệ tươi bạn rửa sạch và đập dập, chia nghệ thành 2 phần. Một phần để luộc chuối, riêng phần để ướp ốc thì bạn băm nhỏ.";

                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Bào bỏ vỏ chuối xanh, cắt lát dày 1 lóng tay. Ngâm với nước giấm loãng cho sạch nhựa trong 10 phút rồi xả lại với nước sạch.";

                }});
                add(new Directions() {{
                    Id = 5;
                    Name = "Chuối sau khi sơ chế thì bạn mang đi luộc với phần nghệ đập dập trong 5 phút. Vớt ra để nguội.";

                }});
                add(new Directions() {{
                    Id = 6;
                    Name = "Đậu hũ cắt thành những miếng vuông nhỏ, chiên vàng đều các mặt. Lá lốt, tía tô rửa sạch, cắt khúc nhỏ. Hành tím bóc vỏ rồi băm nhuyễn. Phần cơm mẻ bạn rây bỏ phần xác, lấy nước.";

                }});
                add(new Directions() {{
                    Id = 7;
                    Name = "Pha 2 muỗng canh bột bắp cùng 100ml nước lọc vào một cái chén và khuấy đều.";


                }});
                add(new Directions() {{
                    Id = 8;
                    Name = "Cho ốc vào tô ướp với 1/2 số hành tím băm, 2 muỗng cà phê tiêu, nghệ băm, 1 muỗng cà phê bột ngọt, 2 muỗng cà phê đường, 3 muỗng cà phê hạt nêm, 1 muỗng cà phê nước mắm.";

                }});
                add(new Directions() {{
                    Id = 9;
                    Name = "Cho 3 muỗng canh dầu ăn vào nồi và đun nóng rồi cho hết phần hành tím còn lại vào phi thơm.";

                }});
                add(new Directions() {{
                    Id = 10;
                    Name = "Tiếp theo bạn cho hết phần ốc đã ướp gia vị vào xào nhanh cùng 3 muỗng canh mẻ đã lọc, xào với lửa vừa đến khi sệt nước";

                }});
                add(new Directions() {{
                    Id = 11;
                    Name = "Sau đó bạn thêm 500ml nước lọc vào, nấu nhỏ lửa trong 15 phút rồi cho chuối luộc, đậu hũ chiên vào, nấu tiếp 5 phút.";

                }});
                add(new Directions() {{
                    Id = 12;
                    Name = "Nêm nếm lại vừa ăn theo khẩu vị mỗi nhà, cho phần bột bắp đã pha vào chờ cho nước sôi lại, khi cảm thấy nước có độ sánh sệt nhẹ thì bạn cho hết phần rau tía tô, lá lốt đã thái khúc vào nồi, tắt bếp là hoàn thành món ăn.";

                }});
            }};
            View = 0;
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/2021/06/CookRecipe/GalleryStep/thanh-pham-175.jpg");
                add("https://cdn.tgdd.vn/2021/06/CookProduct/1200-1200x676-4.jpg");
                add("https://cdn.tgdd.vn/2021/06/content/nguyenlieu-800x450.jpg");

            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_017";
            Name = "Cánh gà chiên nước mắm";
            Description = "Cánh gà chiên nước mắm là món chiên thơm ngon, đậm vị, được rất nhiều gia đình ưa chuộng với độ thơm ngon đặc trưng, mặn bùi cực bắt cơm.";
            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/2021/08/CookRecipe/GalleryStep/canh-ga-chien-nuoc-mam-cong-thuc-duoc-chia-se-tu-nguoi-dung-9.jpg";
            RecipeAuth = "user_id_test_member17";
            PrepareTime = new PrepareTime() {{
                Time = "30";
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
                    Name = "Cánh gà";
                    Quantitative = 300;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Rau xà lách";
                    Quantitative = 200;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Bột mì";
                    Quantitative = 20;
                }});

                add(new Ingredients() {{
                    Id = 4;
                    Name = "Muối, dầu hào, đường, hạt nêm, hạt tiêu, tỏi";
                    Quantitative = 15;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "Cánh gà mua về rửa thật sạch với nước và để ra rổ cho ráo, để cánh gà khi chiên được giòn và ngon hơn bạn cho 2 muỗng canh bột mì vào trộn đều và để trong vòng 5 phút.";

                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Xà lách mua về nhặt từng lá, rửa sạch 2 - 3 lần với nước và để ra rổ. Hành và tỏi lột vỏ, rửa sơ qua nước và băm nhuyễn.";

                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Đầu tiên bắc chảo lên bếp, sau đó cho 3 muỗng canh dầu ăn vào và đun với lửa nhỏ. Tiếp đến lấy đũa thử dầu, nếu nổi bong bóng là dầu đã nóng, lúc này mình mới cho cánh gà vào chiên.";

                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Tiếp tục chiên cánh gà cho đến khi vàng giòn đều hết hai mặt, sau đó cho ra dĩa và tắt bếp.";

                }});
                add(new Directions() {{
                    Id = 5;
                    Name = "Phần dầu ăn trong chảo bạn lấy ra, chỉ để còn 1 lượng ít đủ để xào hành và tỏi. Sau khi phi hành tỏi đã vàng thơm rồi thì cho vào chảo 1 muỗng canh nước mắm và 1 muỗng canh đường.";

                }});
                add(new Directions() {{
                    Id = 6;
                    Name = "Các bạn đun với lửa vừa đến khi nước sốt sánh mịn lại, nêm nếm gia vị sao cho vừa ăn thì tắt bếp, trang trí thêm một ít rau xà lách và rưới nước sốt lên cánh gà.";

                }});

            }};
            View = 0;
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/2021/08/CookRecipe/GalleryStep/canh-ga-chien-nuoc-mam-cong-thuc-duoc-chia-se-tu-nguoi-dung-9.jpg");
                add("https://cdn.tgdd.vn/2021/08/CookRecipe/Avatar/canh-ga-chien-nuoc-mam-cong-thuc-duoc-chia-se-tu-nguoi-dung-thumbnail-2.jpg");
                add("https://cdn.tgdd.vn/2021/08/content/nglieu-800x450-9.jpg");

            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_018";
            Name = "Ốc hương sốt trứng muối";
            Description = "Đối với các tín đồ của các món ốc thì chắc chắn không thể bỏ lỡ các món ngon từ ốc hương. Hôm nay, mình sẽ hướng dẫn bạn làm món ăn này tại nhà cực ngon nhé!";
            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/2021/12/CookRecipe/Avatar/oc-huong-sot-trung-muoi-cong-thuc-duoc-chia-se-tu-nguoi-dung-thumbnail.jpg";
            RecipeAuth = "user_id_test_member18";
            PrepareTime = new PrepareTime() {{
                Time = "60";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "40";
                TimeType = "m";
            }};
            Level = "Easy";
            Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                add(new Ingredients() {{
                    Id = 1;
                    Name = "Ốc hương";
                    Quantitative = 400;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Lòng đỏ trứng muối";
                    Quantitative = 60;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Rượu trắng";
                    Quantitative = 100;
                }});

                add(new Ingredients() {{
                    Id = 4;
                    Name = " Muối, Ớt, Tỏi, Mắm, Dầu ăn, Dường, Bơ lạt";
                    Quantitative = 15;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "Ốc hương bạn cho vào tô lớn, sau đó thêm 1 lít nước, 2 trái ớt cắt lát, đảo sơ và ngâm trong khoảng 1 - 2 tiếng cho ốc sạch.";

                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Bắc nồi nước lên bếp rồi cho vào 1 củ gừng cắt lát, 1 muỗng cà phê muối. Đợi khi nước sôi lên thì cho ốc hương vào luộc với lửa riu riu tầm 10 phút, sau đó tắt bếp rồi vớt ốc ra.";

                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Cho trứng muối vào 1 cái tô, thêm 100ml rượu trắng, rửa sơ, sau đó vớt trứng muối ra cho vào xưng hấp tầm 10 phút. Khi trứng muối chín, bạn lấy ra để nguội rồi tán cho nhuyễn.";

                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Bắc chảo lên bếp, đợi chảo nóng lên thì cho vào 1 muỗng canh dầu ăn, 1 muỗng canh tỏi băm, đảo đều dưới lửa riu riu.";

                }});
                add(new Directions() {{
                    Id = 5;
                    Name = "Thấy tỏi đã thơm vàng thì cho trứng muối tán nhuyễn vào, thêm 20ml nước lọc, 2 muỗng canh đường, 1 muỗng canh nước mắm, 40gr bơ lạt, 2 trái ớt cắt lát rồi đảo đều trên lửa nhỏ.";

                }});
                add(new Directions() {{
                    Id = 6;
                    Name = "Khi thấy bơ chảy hết, sốt hơi đặc lại thì bạn cho ốc hương vào, đảo đều tầm 2 - 3 phút cho ốc thấm đều nước sốt rồi tắt bếp.";

                }});

            }};
            View = 0;
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/2021/12/CookRecipe/GalleryStep/thanh-pham-1009.jpg");
                add("hhttps://cdn.tgdd.vn/2021/12/CookRecipe/GalleryStep/thanh-pham-1093.jpg");
                add("https://cdn.tgdd.vn/2021/12/CookRecipe/Avatar/oc-huong-sot-trung-muoi-cong-thuc-duoc-chia-se-tu-nguoi-dung-thumbnail.jpg");

            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_019";
            Name = "Pizza hải sản phô mai";
            Description = "Pizza hải sản phô mai là một món nướng với sự kết hợp hài hoà giữa vị tươi ngon của hải sản và vị thơm béo từ phô mai tạo nên vị ngon khó cưỡng, chắc chắn sẽ khiến bạn thích mê.";
            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/2020/09/CookProduct/1200bzhspm-1200x676.jpg";
            RecipeAuth = "user_id_test_member19";
            PrepareTime = new PrepareTime() {{
                Time = "20";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "80";
                TimeType = "m";
            }};
            Level = "Easy";
            Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                add(new Ingredients() {{
                    Id = 1;
                    Name = "Đế bánh Pizza";
                    Quantitative = 300;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Tôm sú";
                    Quantitative = 300;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Mực ống";
                    Quantitative = 200;
                }});
                add(new Ingredients() {{
                    Id = 4;
                    Name = "Ớt chuông";
                    Quantitative = 150;
                }});
                add(new Ingredients() {{
                    Id = 5;
                    Name = "Phô mai Mozzarella";
                    Quantitative = 100;
                }});

                add(new Ingredients() {{
                    Id = 6;
                    Name = "Bơ, Tương cà, Tương ớt, Hành";
                    Quantitative = 15;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "Tôm bạn lột sạch vỏ, loại bỏ hết đầu và đuôi, dùng dao rạch 1 đường trên lưng tôm để dễ dàng lấy hết phần chỉ tôm.";

                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Mực ống mua về lột sạch phần da bên ngoài, rửa sạch và cắt khoanh tròn là được.";

                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Cho vào phần tôm và mực 1/2 thìa muối, 1/2 thìa đường, 1/2 thìa tiêu và 1 thìa hạt nêm. Trộn đều và để yên cho thấm vị trong khoảng 10 phút.";

                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Ớt chuông mua về rửa sạch, cắt miếng vuông nhỏ khoảng 1 - 2 lóng tay. Hành tím cắt lát dạng khoanh tròn. Ớt cắt lát mỏng, loại hết hạt đi là được.";

                }});
                add(new Directions() {{
                    Id = 5;
                    Name = "Bắc lên bếp 1 cái chảo chờ nóng phi thơm hành và tỏi băm. Cho hết hải sản vào xào với lửa lớn cho săn đều là có thể tắt bếp.";

                }});
                add(new Directions() {{
                    Id = 6;
                    Name = "Phết đều 1 lớp tương cà lên trên mặt đế bánh pizza, xếp lên trên lần lượt hết phần hành tím, ớt chuông, phô mai trải đều mặt bánh.";

                }});
                add(new Directions() {{
                    Id = 7;
                    Name = "Thêm hải sản và vài lát ớt lên trên và chuẩn bị nướng, có thể thêm 1 lớp phô mai nữa ở trên cùng nếu thích.";


                }});
                add(new Directions() {{
                    Id = 8;
                    Name = "Đầu tiên, bạn cần phải mở lò nướng trước để làm nóng lò, mở ở nhiệt độ 200 độ C trong vòng 10 - 15 phút. Khi đủ thời gian cho bánh vào nướng ở nhiệt độ 230 độ C trong khoảng 7 phút là được.";

                }});

            }};
            View = 0;
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/2020/09/CookRecipe/GalleryStep/thanh-pham-297.jpg");
                add("https://cdn.tgdd.vn/2020/09/CookRecipe/GalleryStep/thanh-pham-298.jpg");
                add("https://cdn.tgdd.vn/2020/09/content/1200nlvc-800x450.jpg");

            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_020";
            Name = "Mì xào hải sản";
            Description = "Mì xào hải sản là món xào thơm ngon, hấp dẫn và cực kì phù hợp để thay đổi khẩu vị cho bữa cơm gia đình thêm mới lạ. Cùng mình vào bếp để học ngay các cách làm mì xào hải sản chiêu đãi cả gia đình nhé!";
            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/2022/07/CookDish/2-cach-lam-mi-xao-hai-san-thom-ngon-hap-dan-doi-vi-cho-bua-an-avt-1200x676.jpg";
            RecipeAuth = "user_id_test_member20";
            PrepareTime = new PrepareTime() {{
                Time = "30";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "20";
                TimeType = "m";
            }};
            Level = "Easy";
            Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                add(new Ingredients() {{
                    Id = 1;
                    Name = "Mì (2 vắt)";
                    Quantitative = 100;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Tôm";
                    Quantitative = 100;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Mực";
                    Quantitative = 200;
                }});
                add(new Ingredients() {{
                    Id = 4;
                    Name = "Ớt chuông";
                    Quantitative = 70;
                }});
                add(new Ingredients() {{
                    Id = 5;
                    Name = "Cải thìa";
                    Quantitative = 100;
                }});

                add(new Ingredients() {{
                    Id = 6;
                    Name = "Dầu ăn, Đường, Hạt nêm, Tiêu xay";
                    Quantitative = 15;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "100gr tôm bạn mua về rửa sạch với nước và để ráo. Sau đó bóc hết vỏ, ngắt bỏ phần đầu tôm và dùng mũi dao nhọn rạch sống lưng của tôm để loại bỏ phần chỉ đen. Rửa sơ lại với nước rồi để ráo và cho tôm vào đĩa.";

                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Với 200gr mực tươi bạn dùng tay rút phần đầu mực ra, bỏ túi mực và rửa sạch lại cả trong lẫn ngoài con mực. Sau đó, bạn dùng dao thái mực thành những khoanh tròn mỏng có độ rộng khoảng một đốt ngón tay.";

                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Ớt chuông thì bạn bỏ cuống, cắt dọc quả để loại bỏ hạt rồi rửa sạch và thái sợi dài mỏng.";

                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "100gr cải thìa, bạn tách riêng từng lá rồi rửa sạch với nước.";

                }});
                add(new Directions() {{
                    Id = 5;
                    Name = "2 vắt mì thì bạn đem ngâm trong tô nước lạnh khoảng 10 phút rồi vớt ra rổ để ráo.";

                }});
                add(new Directions() {{
                    Id = 6;
                    Name = "Bạn cho tôm và mực đã sơ chế vào chung một tô rồi thêm vào các gia vị gồm 1 muỗng canh hạt nêm, 1/2 muỗng cà phê đường, 1/2 muỗng cà phê bột ngọt, 1/2 muỗng cà phê tiêu xay. Bạn trộn đều rồi ướp tôm và mực trong khoảng 15 phút cho ngấm gia vị.";

                }});
                add(new Directions() {{
                    Id = 7;
                    Name = "Bạn bắc nồi lên bếp và đổ nước vào 1/2 nồi. Đun đến khi nước sôi thì cho 2 vắt mì vào chần khoảng 30s rồi tắt bếp. Tiếp đó, bạn vớt mì ra rổ và xả sơ với nước lạnh cho sợi mì dai hơn và để ráo.";


                }});
                add(new Directions() {{
                    Id = 8;
                    Name = "Bạn bắc chảo lên bếp, cho vào 1 muỗng canh dầu ăn và phi thơm tỏi băm. Tiếp đến cho tôm, mực đã ướp gia vị vào xào ở mức lửa lớn đến khi gần chín thì cho ra đĩa.";

                }});
                add(new Directions() {{
                    Id = 9;
                    Name = "Kế đến, bạn tiếp tục cho 1 muỗng canh dầu ăn vào ngay chảo đang nấu. Khi dầu nóng lên, bạn cho ớt chuông đã sơ chế vào xào. Kế đến, bạn cho vào 1 ít nước lọc, 1 muỗng canh hạt nêm, 1/2 muỗng cà phê đường và xào ở mức lửa vừa phải trong 5 phút.";

                }});
                add(new Directions() {{
                    Id = 10;
                    Name = "Khi thấy ớt chuông vừa chín tới thì cho tiếp cải thìa vào chảo, đảo đều tay. Đợi cải thìa hơi mềm thì đổ tiếp mì vào xào chung cho thấm đều gia vị.";

                }});
                add(new Directions() {{
                    Id = 11;
                    Name = "Cuối cùng, bạn cho tôm, mực đã xào vào chảo và tiếp tục xào ở lửa lớn thêm khoảng 1 phút rồi nêm nếm lại gia vị cho phù hợp với khẩu vị của gia đình. Sau đó bạn tắt bếp là xong.";

                }});

            }};
            View = 0;
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/2022/07/CookRecipe/GalleryStep/thanh-pham-4.jpg");
                add("https://cdn.tgdd.vn/2022/05/CookRecipe/Avatar/mi-tom-xao-hai-san-cong-thuc-chia-se-tu-nguoi-dung-thumbnail.jpg");
                add("https://cdn.tgdd.vn/2022/07/CookRecipe/GalleryStep/thanh-pham-5.jpg");

            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_021";
            Name = "Canh chua ngao";
            Description = "Canh chua ngao chắc chắn sẽ là lựa chọn tuyệt vời cho bữa cơm gia đình bạn vào những ngày oi bức đấy nhé! Trở về nhà sau một ngày làm việc chăm chỉ và thưởng thức món canh này với cơm nóng hoặc bún tươi thì còn gì bằng. Cùng vào bếp với mình để xem qua 4 công thức nấu canh chua ngao dưới đây nhé!";
            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/Files/2019/09/08/1196161/huong-dan-cach-lam-mieng-mang-vit-thom-ngon-don-gian-tai-nha-10-760x367.jpg";
            RecipeAuth = "user_id_test_member20";
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
                    Name = "Ngao";
                    Quantitative = 1000;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Cà chua";
                    Quantitative = 120;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Rau muống";
                    Quantitative = 150;
                }});
                add(new Ingredients() {{
                    Id = 4;
                    Name = "Xả";
                    Quantitative = 80;
                }});
                add(new Ingredients() {{
                    Id = 5;
                    Name = "Me chua";
                    Quantitative = 50;
                }});

                add(new Ingredients() {{
                    Id = 6;
                    Name = "Muối, Hạt nêm, Dầu ăn, Ớt sừng, Lá chanh";
                    Quantitative = 15;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "Ngao mua về, bạn vo mạnh tay trực tiếp dưới vòi nước rồi ngâm với nước muối ớt pha loãng trong khoảng 3 - 4 tiếng để ngao nhả bùn cát. Sau đó lại chà sạch vỏ ngao rồi rửa lại với nhiều lần nước.";

                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Rửa sạch cà chua, lá chanh rồi thái múi cau cà chua. Gừng tươi gọt vỏ rồi thái lát mỏng.";

                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Rửa sạch sả, cắt bỏ phần ngọn giữ lại gốc sả, đập dập để ra mùi tốt hơn. Rửa sạch rau muống và cắt thành đoạn dài khoảng 1 ngón tay.";

                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Bắc nồi lên bếp, phi thơm cà chua với 1 muỗng canh dầu ăn trên lửa nhỏ, sau khoảng 3 phút khi cà chua đã ra màu thì bắt đầu cho lá chanh vào, đảo nhanh tay để cà chua không bị cháy.";

                }});
                add(new Directions() {{
                    Id = 5;
                    Name = "Cho vào nồi cà chua 1.5 lít nước lọc rồi tăng lửa lớn, khi nước sôi nhẹ thì cho sả và gừng đã sơ chế vào.";

                }});
                add(new Directions() {{
                    Id = 6;
                    Name = "Cho vắt me chua vào tô, lấy ít nước canh nóng cho vào rồi dầm nát. Khi me tan thì chắt lấy nước me cho vào canh và bỏ hạt. Nêm canh với 1 ít muối và 1/3 muỗng cà phê hạt nêm.";

                }});
                add(new Directions() {{
                    Id = 7;
                    Name = "Khi nước canh sôi kĩ thì cho thịt ngao vào, đậy nắp nồi lại để đun tiếp cho canh sôi trong khoảng 4 phút là được.";


                }});
                add(new Directions() {{
                    Id = 8;
                    Name = "Bắc một cái nồi khác với khoảng 300ml nước lạnh lên bếp và nấu nước sôi với lửa lớn.";

                }});
                add(new Directions() {{
                    Id = 9;
                    Name = "Nước sôi thì cho rau vào luộc xanh đều thì vớt ra tô. Sau đó múc thêm thịt ngao và nước canh chua vào cùng để dùng ngay khi nóng.";

                }});

            }};
            View = 0;
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/2021/11/CookRecipe/GalleryStep/thanh-pham-1788.jpg");
                add("https://cdn.tgdd.vn/2021/11/CookRecipe/Avatar/canh-ngao-ngheu-nau-sau-thumbnail-2.jpg");
                add("https://cdn.tgdd.vn/2021/11/CookRecipe/CookUtensilandNotes/canh-ngao-ngheu-chua-cay-note-1200x676.jpg");

            }};
        }});
        listData.add(new Recipe() {{
            Id = "Recipe_022";
            Name = "Canh ngao nấu sấu";
            Description = "Canh ngao nấu sấu chắc chắn sẽ là lựa chọn tuyệt vời cho bữa cơm gia đình bạn vào những ngày oi bức đấy nhé! Trở về nhà sau một ngày làm việc chăm chỉ và thưởng thức món canh này với cơm nóng hoặc bún tươi thì còn gì bằng. Cùng vào bếp với mình để xem qua 4 công thức nấu canh chua ngao dưới đây nhé!";
            TimeInit = "" + System.currentTimeMillis();
            ImgUrl = "https://cdn.tgdd.vn/2021/11/CookRecipe/Avatar/canh-ngao-ngheu-nau-sau-thumbnail-1.jpg";
            RecipeAuth = "user_id_test_member19";
            PrepareTime = new PrepareTime() {{
                Time = "15";
                TimeType = "m";
            }};
            CookTime = new CookTime() {{
                Time = "40";
                TimeType = "m";
            }};
            Level = "Easy";
            Ingredients = new ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients>() {{
                add(new Ingredients() {{
                    Id = 1;
                    Name = "Ngao";
                    Quantitative = 600;
                }});
                add(new Ingredients() {{
                    Id = 2;
                    Name = "Sấu";
                    Quantitative = 50;
                }});
                add(new Ingredients() {{
                    Id = 3;
                    Name = "Cà chua";
                    Quantitative = 70;
                }});


                add(new Ingredients() {{
                    Id = 4;
                    Name = "Muối, Hạt nêm, Dầu ăn, Mắm, Thì là, Rau răm, Hành, Ớt";
                    Quantitative = 15;
                }});

            }};
            Directions = new ArrayList<doan.npnm.sharerecipe.model.recipe.Directions>() {{
                add(new Directions() {{
                    Id = 1;
                    Name = "Ngao mua về ngâm với nước pha loãng 1 muỗng canh muối với vài lát ớt cho ngao nhả cát. Sau đó chà rửa sạch vỏ ngao rồi cho vào nồi luộc khoảng 10 phút cho ngao há hết miệng.";

                }});
                add(new Directions() {{
                    Id = 2;
                    Name = "Hành tím bỏ vỏ và băm nhỏ. Gừng cạo sạch vỏ và đập dập. Rau răm, thì là và hành lá nhặt rửa sạch rồi thái nhỏ. Cà chua rửa sạch rồi thái miếng mỏng. Sấu cạo bỏ vỏ, rửa sạch rồi đập dập.";

                }});
                add(new Directions() {{
                    Id = 3;
                    Name = "Bắc nồi lên bếp, làm nóng 1 muỗng canh dầu ăn với lửa vừa rồi cho hành tím vào đảo nhanh tay khoảng 1 phút.";

                }});
                add(new Directions() {{
                    Id = 4;
                    Name = "Bắc nồi lên bếp, phi thơm cà chua với 1 muỗng canh dầu ăn trên lửa nhỏ, sau khoảng 3 phút khi cà chua đã ra màu thì bắt đầu cho lá chanh vào, đảo nhanh tay để cà chua không bị cháy.";

                }});
                add(new Directions() {{
                    Id = 5;
                    Name = "Khi hành vàng thơm thì cho cà chua vào xào chín mềm trong khoảng 3 phút rồi nêm thêm 1 muỗng cà phê hạt nêm vào.";

                }});
                add(new Directions() {{
                    Id = 6;
                    Name = "Sau đó, cho thịt ngao và 1 muỗng canh nước mắm vào đảo qua khoảng 3 phút nữa rồi cho sấu đập dập vào đảo đều. Úp nắp nồi lại khoảng 5 phút để các nguyên liệu hòa quyện với nhau.";

                }});
                add(new Directions() {{
                    Id = 7;
                    Name = "Gạn phần nước luộc ngao trong, sạch vào nồi, chừa lại phần cặn bẩn. Đun sôi trong khoảng 2 - 3 phút, sau đó cho rau thơm đã thái nhỏ vào.";


                }});
                add(new Directions() {{
                    Id = 8;
                    Name = "Nêm nếm lại gia vị sao cho vừa miệng rồi tắt bếp, cho canh ngao nấu sấu ra bát để thưởng thức ngay khi còn nóng nhé!";

                }});

            }};
            View = 0;
            Share = 0;
            History = new ArrayList<String>() {{
                add("Add Time=" + getTimeNow());
            }};
            Love = 0;
            RecipeStatus = RecipeStatus.PREVIEW;
            Category = getCategoryID();
            ImagePreview = new ArrayList<String>() {{
                add("https://cdn.tgdd.vn/2021/11/CookRecipe/GalleryStep/thanh-pham-1716.jpg");
                add("https://www.disneycooking.com/wp-content/uploads/2019/11/canh-ngao-nau-sau.jpg");
                add("https://www.disneycooking.com/wp-content/uploads/2019/11/canh-ngao-chua-nau-sau.jpg");

            }};
        }});
        return listData;
    }


}
