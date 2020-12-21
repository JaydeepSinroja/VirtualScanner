package com.example.virtualscanner.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.virtualscanner.Activity.MainActivity;
import com.example.virtualscanner.R;
import com.example.virtualscanner.Utils.Constants;
import com.example.virtualscanner.Utils.StoreUserData;
import com.example.virtualscanner.databinding.FragmentLokerBinding;


public class LokerFragment extends Fragment implements View.OnClickListener {

    private FragmentLokerBinding binding;
    private Activity activity;
    private StoreUserData storeUserData;
    private String password;
    private String confirm_password;
    private  Animation animFade;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLokerBinding.inflate(inflater, container, false);
        activity = getActivity();
        password = "";
        confirm_password = "";
        storeUserData = new StoreUserData(activity);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (storeUserData.getString(Constants.PASSCODE).equals("PASSCODE") || storeUserData.getString(Constants.PASSCODE).isEmpty()) {
            binding.textPromt.setText(R.string.createpasscode);
        }
        animFade = AnimationUtils.loadAnimation(activity, R.anim.shake);
        animFade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.firstCheck.setBackgroundResource(R.drawable.wrong_passcode);
                binding.secondCheck.setBackgroundResource(R.drawable.wrong_passcode);
                binding.thirdCheck.setBackgroundResource(R.drawable.wrong_passcode);
                binding.fourthCheck.setBackgroundResource(R.drawable.wrong_passcode);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.firstCheck.setBackgroundResource(R.drawable.unselected_check_image);
                binding.secondCheck.setBackgroundResource(R.drawable.unselected_check_image);
                binding.thirdCheck.setBackgroundResource(R.drawable.unselected_check_image);
                binding.fourthCheck.setBackgroundResource(R.drawable.unselected_check_image);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                binding.firstCheck.setBackgroundResource(R.drawable.wrong_passcode);
                binding.secondCheck.setBackgroundResource(R.drawable.wrong_passcode);
                binding.thirdCheck.setBackgroundResource(R.drawable.wrong_passcode);
                binding.fourthCheck.setBackgroundResource(R.drawable.wrong_passcode);
            }
        });

        //onClickListner
        binding.button0.setOnClickListener(this);
        binding.button1.setOnClickListener(this);
        binding.button2.setOnClickListener(this);
        binding.button3.setOnClickListener(this);
        binding.button4.setOnClickListener(this);
        binding.button5.setOnClickListener(this);
        binding.button6.setOnClickListener(this);
        binding.button7.setOnClickListener(this);
        binding.button8.setOnClickListener(this);
        binding.button9.setOnClickListener(this);
        binding.cancelLocker.setOnClickListener(this);
        binding.deleteLocker.setOnClickListener(this);


        binding.textWatcher.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (password.length() == 0) {
                    binding.firstCheck.setBackgroundResource(R.drawable.unselected_check_image);
                    binding.secondCheck.setBackgroundResource(R.drawable.unselected_check_image);
                    binding.thirdCheck.setBackgroundResource(R.drawable.unselected_check_image);
                    binding.fourthCheck.setBackgroundResource(R.drawable.unselected_check_image);
                } else if (password.length() == 1) {
                    binding.firstCheck.setBackgroundResource(R.drawable.selected_check_image);
                    binding.secondCheck.setBackgroundResource(R.drawable.unselected_check_image);
                    binding.thirdCheck.setBackgroundResource(R.drawable.unselected_check_image);
                    binding.fourthCheck.setBackgroundResource(R.drawable.unselected_check_image);
                } else if (password.length() == 2) {
                    binding.firstCheck.setBackgroundResource(R.drawable.selected_check_image);
                    binding.secondCheck.setBackgroundResource(R.drawable.selected_check_image);
                    binding.thirdCheck.setBackgroundResource(R.drawable.unselected_check_image);
                    binding.fourthCheck.setBackgroundResource(R.drawable.unselected_check_image);
                } else if (password.length() == 3) {
                    binding.firstCheck.setBackgroundResource(R.drawable.selected_check_image);
                    binding.secondCheck.setBackgroundResource(R.drawable.selected_check_image);
                    binding.thirdCheck.setBackgroundResource(R.drawable.selected_check_image);
                    binding.fourthCheck.setBackgroundResource(R.drawable.unselected_check_image);
                } else if (password.length() == 4) {
                    binding.firstCheck.setBackgroundResource(R.drawable.selected_check_image);
                    binding.secondCheck.setBackgroundResource(R.drawable.selected_check_image);
                    binding.thirdCheck.setBackgroundResource(R.drawable.selected_check_image);
                    binding.fourthCheck.setBackgroundResource(R.drawable.selected_check_image);

                    new Handler().postDelayed(() -> {
                        if (password.equals(storeUserData.getString(Constants.PASSCODE)) && (!storeUserData.getString(Constants.PASSCODE).equals("PASSCODE") || !storeUserData.getString(Constants.PASSCODE).isEmpty())) {
                            LockerShowFragment lockerShowFragment = new LockerShowFragment();
                            ((MainActivity) activity).switchToFragment(lockerShowFragment);
                        } else {
                            if ((storeUserData.getString(Constants.PASSCODE).equals("PASSCODE") || storeUserData.getString(Constants.PASSCODE).isEmpty())) {
                                if (password.length() == 4) {
                                    binding.textPromt.setText(R.string.confirmpasscode);
                                    binding.firstCheck.setBackgroundResource(R.drawable.unselected_check_image);
                                    binding.secondCheck.setBackgroundResource(R.drawable.unselected_check_image);
                                    binding.thirdCheck.setBackgroundResource(R.drawable.unselected_check_image);
                                    binding.fourthCheck.setBackgroundResource(R.drawable.unselected_check_image);
                                }
                            } else {
                                Toast.makeText(activity, "Passcode Incorrect", Toast.LENGTH_SHORT).show();
                                password = "";
                                binding.textWatcher.setText(" xcvws");

                                binding.checkLayout.startAnimation(animFade);
                            }
                        }
                    }, 200);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });


        binding.textWatcher2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (confirm_password.length() == 0) {
                    binding.firstCheck.setBackgroundResource(R.drawable.unselected_check_image);
                    binding.secondCheck.setBackgroundResource(R.drawable.unselected_check_image);
                    binding.thirdCheck.setBackgroundResource(R.drawable.unselected_check_image);
                    binding.fourthCheck.setBackgroundResource(R.drawable.unselected_check_image);
                } else if (confirm_password.length() == 1) {
                    binding.firstCheck.setBackgroundResource(R.drawable.selected_check_image);
                    binding.secondCheck.setBackgroundResource(R.drawable.unselected_check_image);
                    binding.thirdCheck.setBackgroundResource(R.drawable.unselected_check_image);
                    binding.fourthCheck.setBackgroundResource(R.drawable.unselected_check_image);
                } else if (confirm_password.length() == 2) {
                    binding.firstCheck.setBackgroundResource(R.drawable.selected_check_image);
                    binding.secondCheck.setBackgroundResource(R.drawable.selected_check_image);
                    binding.thirdCheck.setBackgroundResource(R.drawable.unselected_check_image);
                    binding.fourthCheck.setBackgroundResource(R.drawable.unselected_check_image);
                } else if (confirm_password.length() == 3) {
                    binding.firstCheck.setBackgroundResource(R.drawable.selected_check_image);
                    binding.secondCheck.setBackgroundResource(R.drawable.selected_check_image);
                    binding.thirdCheck.setBackgroundResource(R.drawable.selected_check_image);
                    binding.fourthCheck.setBackgroundResource(R.drawable.unselected_check_image);
                } else if (confirm_password.length() == 4) {
                    binding.firstCheck.setBackgroundResource(R.drawable.selected_check_image);
                    binding.secondCheck.setBackgroundResource(R.drawable.selected_check_image);
                    binding.thirdCheck.setBackgroundResource(R.drawable.selected_check_image);
                    binding.fourthCheck.setBackgroundResource(R.drawable.selected_check_image);

                    new Handler().postDelayed(() -> {
                        if (confirm_password.equals(password)) {
                            Toast.makeText(activity, "Passcode Created Successfully", Toast.LENGTH_SHORT).show();
                            storeUserData.setString(Constants.PASSCODE, confirm_password);
                            password = "";
                            confirm_password = "";
                            binding.textPromt.setText(R.string.enterpasscode);
                            binding.firstCheck.setBackgroundResource(R.drawable.unselected_check_image);
                            binding.secondCheck.setBackgroundResource(R.drawable.unselected_check_image);
                            binding.thirdCheck.setBackgroundResource(R.drawable.unselected_check_image);
                            binding.fourthCheck.setBackgroundResource(R.drawable.unselected_check_image);
                        } else {
                            Toast.makeText(activity, "Passcode didnt match", Toast.LENGTH_SHORT).show();
                            confirm_password = "";
                            binding.firstCheck.setBackgroundResource(R.drawable.unselected_check_image);
                            binding.secondCheck.setBackgroundResource(R.drawable.unselected_check_image);
                            binding.thirdCheck.setBackgroundResource(R.drawable.unselected_check_image);
                            binding.fourthCheck.setBackgroundResource(R.drawable.unselected_check_image);
                            binding.checkLayout.startAnimation(animFade);
                        }
                    }, 200);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_0:
                if (password.length() < 4) {
                    password = password + 0;
                    binding.textWatcher.setText(password + "0");
                } else {
                    confirm_password = confirm_password + 0;
                    binding.textWatcher2.setText(confirm_password + "0");
                }

                break;
            case R.id.button_1:
                if (password.length() < 4) {
                    password = password + 1;
                    binding.textWatcher.setText(password + "1");
                } else {
                    confirm_password = confirm_password + 1;
                    binding.textWatcher2.setText(confirm_password + "1");
                }

                break;
            case R.id.button_2:
                if (password.length() < 4) {
                    password = password + 2;
                    binding.textWatcher.setText(password + "2");

                } else {
                    confirm_password = confirm_password + 2;
                    binding.textWatcher2.setText(confirm_password + "2");
                }
                break;
            case R.id.button_3:
                if (password.length() < 4) {
                    password = password + 3;
                    binding.textWatcher.setText(password + "3");
                } else {
                    confirm_password = confirm_password + 3;
                    binding.textWatcher2.setText(confirm_password + "3");
                }

                break;
            case R.id.button_4:
                if (password.length() < 4) {
                    password = password + 4;
                    binding.textWatcher.setText(password + "4");
                } else {
                    confirm_password = confirm_password + 4;
                    binding.textWatcher2.setText(confirm_password + "4");
                }

                break;
            case R.id.button_5:
                if (password.length() < 4) {
                    password = password + 5;
                    binding.textWatcher.setText(password + "5");
                } else {
                    confirm_password = confirm_password + 5;
                    binding.textWatcher2.setText(confirm_password + "5");
                }

                break;
            case R.id.button_6:
                if (password.length() < 4) {
                    password = password + 6;
                    binding.textWatcher.setText(password + "6");
                } else {
                    confirm_password = confirm_password + 6;
                    binding.textWatcher2.setText(confirm_password + "6");
                }

                break;
            case R.id.button_7:
                if (password.length() < 4) {
                    password = password + 7;
                    binding.textWatcher.setText(password + "7");
                } else {
                    confirm_password = confirm_password + 7;
                    binding.textWatcher2.setText(confirm_password + "7");
                }

                break;
            case R.id.button_8:
                if (password.length() < 4) {
                    password = password + 8;
                    binding.textWatcher.setText(password + "8");
                } else {
                    confirm_password = confirm_password + 8;
                    binding.textWatcher2.setText(confirm_password + "8");
                }

                break;
            case R.id.button_9:
                if (password.length() < 4) {
                    password = password + 9;
                    binding.textWatcher.setText(password + "9");
                } else {
                    confirm_password = confirm_password + 9;
                    binding.textWatcher2.setText(confirm_password + "9");
                }

                break;
            case R.id.cancel_locker:
                GalleryFragment galleryFragment = new GalleryFragment();
                ((MainActivity) activity).switchToFragment(galleryFragment);
                break;
            case R.id.delete_locker:
                if (binding.textPromt.getText().toString().trim().equals("Create passcode") || binding.textPromt.getText().toString().trim().equals("Enter passcode")) {
                    if (password != null && password.length() > 0) {
                        password = password.substring(0, password.length() - 1);
                        binding.textWatcher.setText(password + "4");
                    }
                } else {
                    if (confirm_password != null && confirm_password.length() > 0) {
                        confirm_password = confirm_password.substring(0, confirm_password.length() - 1);
                        binding.textWatcher2.setText(confirm_password + "4");
                    }
                }

                break;
            default:
                break;
        }
    }
}