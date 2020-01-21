package com.candykick.startup.view.marketAct.addNewMarket;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;

import com.candykick.startup.R;
import com.candykick.startup.view.base.BaseFragment;
import com.candykick.startup.databinding.FragmentAddnewmarketPriceDataBinding;

import java.util.ArrayList;

/*
  2019.08.06 패키지 부분 작동 방식
  Standard 작성 다 되어 있으면 -> Deluxe 열림
  Deluxe 작성 다 되어 있고 Deluxe패키지 사용 상태이면  -> Premium 열림
  이후, 서로 왔다갔다 할 때에도 내용이 전부 작성되어 있어야 함.

  전송 부분
  코드 짜면서 생각하자;;

 */

public class PriceDataFragment extends BaseFragment<FragmentAddnewmarketPriceDataBinding> implements PriceDataInterface {

    String[] strModify = {"불가","1회","2회","3회","4회","5회","6회","7회","8회","9회","10회","11회","12회","13회","14회","15회","제한없음"};
    String[] strDate = {"1일","2일","3일","4일","5일","6일","7일","8일","9일","10일","11일","12일","13일","14일","15일",
    "16일","17일","18일","19일","20일","21일","22일","23일","24일","25일","26일","27일","28일","29일","30일"};

    int page = 0; boolean isDeluxe = false; boolean isPremium = false;
    ArrayList<AddNewMarketPackage> packageData = new ArrayList<>();

    public PriceDataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setFragment(this);

        binding.spANMPackageModify.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, strModify));
        binding.spANMPackageDate.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, strDate));

        binding.cbANMPackageAvailable.setOnCheckedChangeListener((buttonView, isChecked) ->  {
            if(isChecked) {
                binding.etANMPackageTitle.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                binding.etANMPackageDes.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                binding.etANMPackagePrice.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                binding.spANMPackageModify.setEnabled(true);
                binding.spANMPackageModify.setFocusable(true);
                binding.spANMPackageDate.setEnabled(true);
                binding.spANMPackageDate.setFocusable(true);
            } else {
                binding.etANMPackageTitle.setInputType(InputType.TYPE_NULL);
                binding.etANMPackageDes.setInputType(InputType.TYPE_NULL);
                binding.etANMPackagePrice.setInputType(InputType.TYPE_NULL);
                binding.spANMPackageModify.setEnabled(false);
                binding.spANMPackageModify.setFocusable(false);
                binding.spANMPackageDate.setEnabled(false);
                binding.spANMPackageDate.setFocusable(false);
            }

            if(page==1) {
                isDeluxe = isChecked;
            } else if(page==2) {
                isPremium = isChecked;
            }
        });
    }

    //Standard 버튼 클릭 시
    public void NewMarketStandard() {
        if(page != 0) {
            if((page == 1 && isDeluxe) || (page == 2 && isPremium)) {
                if (binding.etANMPackageTitle.getText().toString().replace(" ", "").length() == 0) {
                    makeToast("패키지 제목을 입력해주세요.");
                } else if (binding.etANMPackageDes.getText().toString().replace(" ", "").length() == 0) {
                    makeToast("상세 설명을 입력해주세요.");
                } else if (binding.etANMPackagePrice.getText().toString().length() == 0) {
                    makeToast("가격을 입력해주세요.");
                } else if (binding.spANMPackageModify.getSelectedItem() == null) {
                    makeToast("수정횟수를 선택해주세요.");
                } else if (binding.spANMPackageDate.getSelectedItem() == null) {
                    makeToast("작업기간을 선택해주세요.");
                } else {
                    savePackageData(page);

                    binding.btnANMStandard.setBackgroundColor(getResources().getColor(R.color.set2GrayBlue));
                    binding.btnANMDeluxe.setBackgroundColor(getResources().getColor(R.color.set2White));
                    binding.btnANMPremium.setBackgroundColor(getResources().getColor(R.color.set2White));

                    //Standard 패키지의 내용을 셋팅.
                    page = 0;
                    binding.cbANMPackageAvailable.setVisibility(View.GONE);
                    binding.etANMPackageTitle.setText(packageData.get(0).infotitle);
                    binding.etANMPackageDes.setText(packageData.get(0).infoexp);
                    binding.etANMPackagePrice.setText(packageData.get(0).price);
                    binding.spANMPackageModify.setSelection(Integer.parseInt(packageData.get(0).infomodnum));
                    binding.spANMPackageDate.setSelection(Integer.parseInt(packageData.get(0).infoworkdate) - 1);
                }
            } else {
                //해당 페이지가 비활성화된 경우
                binding.btnANMStandard.setBackgroundColor(getResources().getColor(R.color.set2GrayBlue));
                binding.btnANMDeluxe.setBackgroundColor(getResources().getColor(R.color.set2White));
                binding.btnANMPremium.setBackgroundColor(getResources().getColor(R.color.set2White));

                //Standard 패키지의 내용을 셋팅.
                page = 0;
                binding.cbANMPackageAvailable.setVisibility(View.GONE);
                binding.etANMPackageTitle.setText(packageData.get(0).infotitle);
                binding.etANMPackageDes.setText(packageData.get(0).infoexp);
                binding.etANMPackagePrice.setText(packageData.get(0).price);
                binding.spANMPackageModify.setSelection(Integer.parseInt(packageData.get(0).infomodnum));
                binding.spANMPackageDate.setSelection(Integer.parseInt(packageData.get(0).infoworkdate) - 1);
            }
        }
    }

    //Deluxe 버튼 클릭 시
    public void NewMarketDeluxe() {
        if(page != 1) {
            if ((page == 2 && !isPremium)) {
                binding.btnANMStandard.setBackgroundColor(getResources().getColor(R.color.set2White));
                binding.btnANMDeluxe.setBackgroundColor(getResources().getColor(R.color.set2GrayBlue));
                binding.btnANMPremium.setBackgroundColor(getResources().getColor(R.color.set2White));

                page = 1;

                //Deluxe 패키지를 작성한 적이 있다: 정보 불러오기
                binding.cbANMPackageAvailable.setVisibility(View.VISIBLE);
                binding.cbANMPackageAvailable.setChecked(isDeluxe);
                binding.etANMPackageTitle.setText(packageData.get(1).infotitle);
                binding.etANMPackageDes.setText(packageData.get(1).infoexp);
                binding.etANMPackagePrice.setText(packageData.get(1).price);
                binding.spANMPackageModify.setSelection(Integer.parseInt(packageData.get(0).infomodnum));
                binding.spANMPackageDate.setSelection(Integer.parseInt(packageData.get(0).infoworkdate) - 1);

                if (isDeluxe) {
                    binding.etANMPackageTitle.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                    binding.etANMPackageDes.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                    binding.etANMPackagePrice.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                    binding.spANMPackageModify.setEnabled(true);
                    binding.spANMPackageModify.setFocusable(true);
                    binding.spANMPackageDate.setEnabled(true);
                    binding.spANMPackageDate.setFocusable(true);
                } else {
                    binding.etANMPackageTitle.setInputType(InputType.TYPE_NULL);
                    binding.etANMPackageDes.setInputType(InputType.TYPE_NULL);
                    binding.etANMPackagePrice.setInputType(InputType.TYPE_NULL);
                    binding.spANMPackageModify.setEnabled(false);
                    binding.spANMPackageModify.setFocusable(false);
                    binding.spANMPackageDate.setEnabled(false);
                    binding.spANMPackageDate.setFocusable(false);
                }
            } else {
                //Standard 작성 다 되어 있으면 -> Deluxe 열림, 서로 왔다갔다 할 때에도 내용이 전부 작성되어 있어야 함.
                if (binding.etANMPackageTitle.getText().toString().replace(" ", "").length() == 0) {
                    makeToast("패키지 제목을 입력해주세요.");
                } else if (binding.etANMPackageDes.getText().toString().replace(" ", "").length() == 0) {
                    makeToast("상세 설명을 입력해주세요.");
                } else if (binding.etANMPackagePrice.getText().toString().length() == 0) {
                    makeToast("가격을 입력해주세요.");
                } else if (binding.spANMPackageModify.getSelectedItem() == null) {
                    makeToast("수정횟수를 선택해주세요.");
                } else if (binding.spANMPackageDate.getSelectedItem() == null) {
                    makeToast("작업기간을 선택해주세요.");
                } else {//모든 정보가 입력되있는 경우
                    savePackageData(page);

                    binding.btnANMStandard.setBackgroundColor(getResources().getColor(R.color.set2White));
                    binding.btnANMDeluxe.setBackgroundColor(getResources().getColor(R.color.set2GrayBlue));
                    binding.btnANMPremium.setBackgroundColor(getResources().getColor(R.color.set2White));

                    page = 1;

                    //Deluxe 패키지를 작성한 적이 없다: 신규 페이지 생성
                    if (packageData.size() == 1) {
                        binding.cbANMPackageAvailable.setVisibility(View.VISIBLE);
                        binding.cbANMPackageAvailable.setChecked(isDeluxe);
                        binding.etANMPackageTitle.setText("");
                        binding.etANMPackageDes.setText("");
                        binding.etANMPackagePrice.setText("");
                        binding.spANMPackageModify.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, strModify));
                        binding.spANMPackageDate.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, strDate));

                        binding.etANMPackageTitle.setInputType(InputType.TYPE_NULL);
                        binding.etANMPackageDes.setInputType(InputType.TYPE_NULL);
                        binding.etANMPackagePrice.setInputType(InputType.TYPE_NULL);
                        binding.spANMPackageModify.setEnabled(false);
                        binding.spANMPackageModify.setFocusable(false);
                        binding.spANMPackageDate.setEnabled(false);
                        binding.spANMPackageDate.setFocusable(false);
                    } else {//Deluxe 패키지를 작성한 적이 있다: 정보 불러오기
                        binding.cbANMPackageAvailable.setVisibility(View.VISIBLE);
                        binding.cbANMPackageAvailable.setChecked(isDeluxe);
                        binding.etANMPackageTitle.setText(packageData.get(1).infotitle);
                        binding.etANMPackageDes.setText(packageData.get(1).infoexp);
                        binding.etANMPackagePrice.setText(packageData.get(1).price);
                        binding.spANMPackageModify.setSelection(Integer.parseInt(packageData.get(0).infomodnum));
                        binding.spANMPackageDate.setSelection(Integer.parseInt(packageData.get(0).infoworkdate) - 1);

                        if (isDeluxe) {
                            binding.etANMPackageTitle.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                            binding.etANMPackageDes.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                            binding.etANMPackagePrice.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                            binding.spANMPackageModify.setEnabled(true);
                            binding.spANMPackageModify.setFocusable(true);
                            binding.spANMPackageDate.setEnabled(true);
                            binding.spANMPackageDate.setFocusable(true);
                        } else {
                            binding.etANMPackageTitle.setInputType(InputType.TYPE_NULL);
                            binding.etANMPackageDes.setInputType(InputType.TYPE_NULL);
                            binding.etANMPackagePrice.setInputType(InputType.TYPE_NULL);
                            binding.spANMPackageModify.setEnabled(false);
                            binding.spANMPackageModify.setFocusable(false);
                            binding.spANMPackageDate.setEnabled(false);
                            binding.spANMPackageDate.setFocusable(false);
                        }
                    }
                }
            }
        }
    }

    //Premium 버튼 클릭 시
    public void NewMarketPremium() {
        if(page != 2) {
            if (!(page == 1 && !isDeluxe)) {
                //Standard 작성 다 되어 있으면 -> Deluxe 열림, 서로 왔다갔다 할 때에도 내용이 전부 작성되어 있어야 함.
                if (binding.etANMPackageTitle.getText().toString().replace(" ", "").length() == 0) {
                    makeToast("패키지 제목을 입력해주세요.");
                } else if (binding.etANMPackageDes.getText().toString().replace(" ", "").length() == 0) {
                    makeToast("상세 설명을 입력해주세요.");
                } else if (binding.etANMPackagePrice.getText().toString().length() == 0) {
                    makeToast("가격을 입력해주세요.");
                } else if (binding.spANMPackageModify.getSelectedItem() == null) {
                    makeToast("수정횟수를 선택해주세요.");
                } else if (binding.spANMPackageDate.getSelectedItem() == null) {
                    makeToast("작업기간을 선택해주세요.");
                } else {//모든 정보가 입력되있는 경우
                    savePackageData(page);

                    binding.btnANMStandard.setBackgroundColor(getResources().getColor(R.color.set2White));
                    binding.btnANMDeluxe.setBackgroundColor(getResources().getColor(R.color.set2White));
                    binding.btnANMPremium.setBackgroundColor(getResources().getColor(R.color.set2GrayBlue));

                    page = 2;

                    //Premium 패키지를 작성한 적이 없다: 신규 페이지 생성
                    if (packageData.size() == 2) {
                        binding.cbANMPackageAvailable.setVisibility(View.VISIBLE);
                        binding.cbANMPackageAvailable.setChecked(isPremium);
                        binding.etANMPackageTitle.setText("");
                        binding.etANMPackageDes.setText("");
                        binding.etANMPackagePrice.setText("");
                        binding.spANMPackageModify.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, strModify));
                        binding.spANMPackageDate.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, strDate));

                        binding.etANMPackageTitle.setInputType(InputType.TYPE_NULL);
                        binding.etANMPackageDes.setInputType(InputType.TYPE_NULL);
                        binding.etANMPackagePrice.setInputType(InputType.TYPE_NULL);
                        binding.spANMPackageModify.setEnabled(false);
                        binding.spANMPackageModify.setFocusable(false);
                        binding.spANMPackageDate.setEnabled(false);
                        binding.spANMPackageDate.setFocusable(false);
                    } else {//Premium 패키지를 작성한 적이 있다: 정보 불러오기
                        binding.cbANMPackageAvailable.setVisibility(View.VISIBLE);
                        binding.cbANMPackageAvailable.setChecked(isPremium);
                        binding.etANMPackageTitle.setText(packageData.get(1).infotitle);
                        binding.etANMPackageDes.setText(packageData.get(1).infoexp);
                        binding.etANMPackagePrice.setText(packageData.get(1).price);
                        binding.spANMPackageModify.setSelection(Integer.parseInt(packageData.get(0).infomodnum));
                        binding.spANMPackageDate.setSelection(Integer.parseInt(packageData.get(0).infoworkdate) - 1);

                        if (isPremium) {
                            binding.etANMPackageTitle.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                            binding.etANMPackageDes.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                            binding.etANMPackagePrice.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                            binding.spANMPackageModify.setEnabled(true);
                            binding.spANMPackageModify.setFocusable(true);
                            binding.spANMPackageDate.setEnabled(true);
                            binding.spANMPackageDate.setFocusable(true);
                        } else {
                            binding.etANMPackageTitle.setInputType(InputType.TYPE_NULL);
                            binding.etANMPackageDes.setInputType(InputType.TYPE_NULL);
                            binding.etANMPackagePrice.setInputType(InputType.TYPE_NULL);
                            binding.spANMPackageModify.setEnabled(false);
                            binding.spANMPackageModify.setFocusable(false);
                            binding.spANMPackageDate.setEnabled(false);
                            binding.spANMPackageDate.setFocusable(false);
                        }
                    }
                }
            } else {
                makeToastLong("DELUXE 패키지의 내용이 있어야 PREMIUM 패키지의 내용을 입력하실 수 있습니다.");
            }
        }
    }

    public ArrayList<AddNewMarketPackage> getPackageInfo() {
        if (binding.etANMPackageTitle.getText().toString().replace(" ", "").length() == 0) {
            makeToast("패키지 제목을 입력해주세요.");
            return null;
        } else if (binding.etANMPackageDes.getText().toString().replace(" ", "").length() == 0) {
            makeToast("상세 설명을 입력해주세요.");
            return null;
        } else if (binding.etANMPackagePrice.getText().toString().length() == 0) {
            makeToast("가격을 입력해주세요.");
            return null;
        } else if (binding.spANMPackageModify.getSelectedItem() == null) {
            makeToast("수정횟수를 선택해주세요.");
            return null;
        } else if (binding.spANMPackageDate.getSelectedItem() == null) {
            makeToast("작업기간을 선택해주세요.");
            return null;
        } else {//모든 정보가 입력되있는 경우
            savePackageData(page);

            ArrayList<AddNewMarketPackage> result = new ArrayList<>();
            result.add(packageData.get(0));

            if(isDeluxe)
                result.add(packageData.get(1));
            if(isPremium)
                result.add(packageData.get(2));

            return result;
        }
    }


    //현제 페이지의 내용을 저장함
    public void savePackageData(int page) {
        if(packageData.size() == page) { //현재 페이지 데이터 저장이 안 되어 있음 -> 새로 추가
            String modify = binding.spANMPackageModify.getSelectedItem().toString();
            if(modify.equals("불가"))
                modify = "0회";
            else if(modify.equals("제한없음"))
                modify = "16회";

            packageData.add(new AddNewMarketPackage(binding.etANMPackageTitle.getText().toString(),
                    binding.etANMPackageDes.getText().toString(), binding.etANMPackagePrice.getText().toString(),
                    modify.replace("회",""), binding.spANMPackageDate.getSelectedItem().toString().replace("일","")));
        } else {//현재 페이지 데이터 저장이 되어 있음 -> 수정
            String modify = binding.spANMPackageModify.getSelectedItem().toString();
            if(modify.equals("불가"))
                modify = "0회";
            else if(modify.equals("제한없음"))
                modify = "16회";

            packageData.get(page).infotitle = binding.etANMPackageTitle.getText().toString();
            packageData.get(page).infoexp = binding.etANMPackageDes.getText().toString();
            packageData.get(page).price = binding.etANMPackagePrice.getText().toString();
            packageData.get(page).infomodnum = modify.replace("회","");
            packageData.get(page).infoworkdate = binding.spANMPackageDate.getSelectedItem().toString().replace("일","");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_addnewmarket_price_data;
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    /*
    if(packageData.size() == 0) { //Standard 패키지의 정보가 없는 경우: 정보 추가
                    packageData.add(new AddNewMarketPackage(binding.etANMPackageTitle.getText().toString(),
                            binding.etANMPackageDes.getText().toString(), Integer.parseInt(binding.etANMPackagePrice.getText().toString()),
                            Integer.parseInt(binding.spANMPackageModify.getSelectedItem().toString().replace("회","")),
                            Integer.parseInt(binding.spANMPackageDate.getSelectedItem().toString().replace("회",""))));
                } else if(page == 0) { //Standard 패키지의 정보가 없는 경우: 정보 수정
                    packageData.get(0).infotitle = binding.etANMPackageTitle.getText().toString();
                    packageData.get(0).infoexp = binding.etANMPackageDes.getText().toString();
                    packageData.get(0).price = Integer.parseInt(binding.etANMPackagePrice.getText().toString());
                    packageData.get(0).infomodnum = Integer.parseInt(binding.spANMPackageModify.getSelectedItem().toString().replace("회",""));
                    packageData.get(0).infoworkdate = Integer.parseInt(binding.spANMPackageDate.getSelectedItem().toString().replace("회",""));
                } else if(page == 2) {
                    packageData.get(2).infotitle = binding.etANMPackageTitle.getText().toString();
                    packageData.get(2).infoexp = binding.etANMPackageDes.getText().toString();
                    packageData.get(2).price = Integer.parseInt(binding.etANMPackagePrice.getText().toString());
                    packageData.get(2).infomodnum = Integer.parseInt(binding.spANMPackageModify.getSelectedItem().toString().replace("회",""));
                    packageData.get(2).infoworkdate = Integer.parseInt(binding.spANMPackageDate.getSelectedItem().toString().replace("회",""));
                }
     */
}
