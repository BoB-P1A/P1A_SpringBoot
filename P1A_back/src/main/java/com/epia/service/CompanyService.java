
package com.epia.service;

import com.epia.domain.EvaluationItem;
import com.epia.repo.CompanyRepo;
import com.epia.domain.Company;
import com.epia.dto.CompanyDto;
import com.epia.web.CompanyController.CompanyCreateRequest;
import com.epia.web.CompanyController.CompanyUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class CompanyService {
    private final CompanyRepo companyRepo;

    public CompanyService(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }

    public List<CompanyDto> listCompanies() {
        return companyRepo.findAll().stream().map(CompanyDto::from).toList();
    }

    public Company get(String id) {
        return companyRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("기업을 찾을 수 없습니다: " + id));
    }

    public Company save(Company c) {
        return companyRepo.save(c);
    }

    @Transactional
    public Company createCompany(CompanyCreateRequest request) {
        Company company = new Company();
        company.name = request.getName();
        company.contactName = request.getContactName();
        company.contactPhone = request.getContactPhone();
        company.createdAt = Instant.now();
        company.updatedAt = Instant.now();

        // 빈 리스트 초기화
        company.accounts = new ArrayList<>();
        company.evaluationItems = new ArrayList<>();
        company.processingTasks = new ArrayList<>();
        company.technicalSystems = new ArrayList<>();
        company.securitySystems = new ArrayList<>();

        Company saved = companyRepo.save(company);

        // 기본 평가항목 자동 삽입
        insertDefaultEvaluationItems(saved);

        return companyRepo.save(company);
    }

    @Transactional
    public Company updateCompany(String id, CompanyUpdateRequest request) {
        Company company = companyRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("기업을 찾을 수 없습니다: " + id));

        company.name = request.getName();
        company.contactName = request.getContactName();
        company.contactPhone = request.getContactPhone();
        company.updatedAt = Instant.now();

        return companyRepo.save(company);
    }

    @Transactional
    public void deleteCompany(String id) {
        Company company = companyRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("기업을 찾을 수 없습니다: " + id));

        // 연결된 계정이 있는 경우 경고 로그
        if (company.accounts != null && !company.accounts.isEmpty()) {
            System.out.println("경고: 기업 '" + company.name + "'에 " + company.accounts.size() + "개의 계정이 연결되어 있습니다.");
        }

        companyRepo.deleteById(id);
    }

    /**
     * 기본 평가항목을 회사에 삽입하는 메서드
     */
    private void insertDefaultEvaluationItems(Company company) {
        List<EvaluationItem> defaultItems = createDefaultEvaluationItems(company.id);
        company.evaluationItems = defaultItems;
        companyRepo.save(company);
    }

    /**
     * 평가항목을 최신 기본 데이터로 업데이트
     * 기존 평가항목을 삭제하고 새로운 기본 데이터를 삽입
     */
    @Transactional
    public void updateDefaultEvaluationItems(String companyId) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("기업을 찾을 수 없습니다: " + companyId));

        // 기존 평가항목 초기화
        company.evaluationItems.clear();

        // 새로운 기본 평가항목 삽입
        List<EvaluationItem> defaultItems = createDefaultEvaluationItems(companyId);
        company.evaluationItems = defaultItems;

        company.updatedAt = Instant.now();
        companyRepo.save(company);
    }

    /**
     * 기본 평가항목 데이터를 생성하는 메서드
     * 코드에서 수정 시 이 메서드만 수정하면 됨
     */
    private List<EvaluationItem> createDefaultEvaluationItems(String companyId) {
        List<EvaluationItem> items = new ArrayList<>();

        // 항목 1
        EvaluationItem item1 = new EvaluationItem();
        item1.id = 1;
        item1.companyId = companyId;
        item1.area = "1. 개인정보처리단계(Lifecycle)";
        item1.field = "수집";
        item1.subField = "개인정보 수집의 적합성";
        item1.no = "1.1.1";
        item1.item = "고객의 동의를 받아 개인정보를 수집하는 경우 관련 내용을 고지하고 고객의 동의를 받고 있습니까?";
        item1.riskFactors = "개인정보 수집 이용 동의서에 해당 항목에 대한 내용을 고지하지 않을 경우 법률 위반 및 과태료 처분을 받을 수 있다.";
        item1.improvementGuides = "(1) 개인정보 수집시   [1. 개인정보의 수집·이용 목적 2. 수집하려는 개인정보의 항목 3. 개인정보의 보유 및 이용기간 4. 동의를 거부할 권리가 있다는 사실 및 동의 거부에 따른 불이익이 있는 경우 그 불이익의 내용] 중 빠진 항목이 있는 경우 해당 항목을 추가해야한다 (2) 4가지 항목에 대해 고지하였으나 각 항목에 대한 내용이 명확하지 않을 경우 해당 항목을 자세히 작성해야한다.";
        item1.law = "개인정보 보호법 제15조(개인정보의 수집ㆍ이용 ) 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item1);

        // 항목 2
        EvaluationItem item2 = new EvaluationItem();
        item2.id = 2;
        item2.companyId = companyId;
        item2.area = "1. 개인정보처리단계(Lifecycle)";
        item2.field = "수집";
        item2.subField = "개인정보 수집의 적합성";
        item2.no = "1.1.2";
        item2.item = "수집 이용 동의서에 중요한 내용은 명확히 표시하고 있습니까? (*고유식별정보, 민감정보, 마케팅, 제3자 제공 등)";
        item2.riskFactors = "개인정보 수집 동의서에 중요한 내용을 명확히 표시하지않고 수집 받을 시 법률 위반 및 과태료 처분을 받을 수 있다.";
        item2.improvementGuides = "(1) 재화 또는 서비스의 홍보를 목적으로 개인정보를 수집하면서 해당 내용을 명확히 표시해야한다. (2) 명확히 표시해야하는 내용은 다른 내용보다 글 크기, 굵기, 밑줄 등을 이용하여 표시하는 방법이 있다.";
        item2.law = "개인정보 보호법 제22조제2항(동의를 받는 방법) 1천만원 이하의 과태료";
        items.add(item2);

        // 항목 3
        EvaluationItem item3 = new EvaluationItem();
        item3.id = 3;
        item3.companyId = companyId;
        item3.area = "1. 개인정보처리단계(Lifecycle)";
        item3.field = "수집";
        item3.subField = "개인정보 수집의 적합성";
        item3.no = "1.1.3";
        item3.item = "수집하는 개인정보 항목은 목적에 부합한 최소한의 항목으로 구성되어 있습니까?";
        item3.riskFactors = "필요 이상의 개인정보를 수집할 경우 정보주체의 권리가 침해될 수 있다.";
        item3.improvementGuides = "(1) 수집하는 개인정보의 목적을 명확히 설정하고 해당 서비스를 제공하기 위해 필수적으로 필요한 항목만 수집해야한다. (2) 개인정보를 수집하는 목적을 고객이 이해할 수 있도록 명확히 설명해야한다. (3) 수집 목적에 부합하지 않는 개인정보를 수집할 경우 해당 항목을 수집하지 않도록 수정해야한다.";
        item3.law = "개인정보 보호법 제16조제1항(개인정보의 수집 제한) 벌칙 없음";
        items.add(item3);

        // 항목 4
        EvaluationItem item4 = new EvaluationItem();
        item4.id = 4;
        item4.companyId = companyId;
        item4.area = "1. 개인정보처리단계(Lifecycle)";
        item4.field = "수집";
        item4.subField = "개인정보 수집의 적합성";
        item4.no = "1.1.4";
        item4.item = "업무목적상 필수로 필요한 항목이 아닌 경우 별도로 분리해서 동의(선택)를 받고 있습니까?";
        item4.riskFactors = "목적에 필요한 최소한의 개인정보외의 항목을 수집할때 별도 동의를 받지 않는 경우 법률 위반 및 과태료 처분을 받을 수 있다.";
        item4.improvementGuides = "(1) 업무상 필수로 수집해야하는 개인정보가 아닐 경우 선택항목으로 구분하여 동의를 받도록 수정해야한다.";
        item4.law = "개인정보 보호법 제16조제3항(개인정보의 수집 제한)·제22조제5항(동의를 받는 방법) 3천만원 이하의 과태료";
        items.add(item4);

        // 항목 5
        EvaluationItem item5 = new EvaluationItem();
        item5.id = 5;
        item5.companyId = companyId;
        item5.area = "1. 개인정보처리단계(Lifecycle)";
        item5.field = "수집";
        item5.subField = "개인정보 수집의 적합성";
        item5.no = "1.1.5";
        item5.item = "마케팅(광고·판매 권유) 목적으로 고객의 개인정보를 사용하려는 경우, 다른 개인정보와 구분하여 별도로 동의를 받고 있습니까?";
        item5.riskFactors = "마케팅을 목적으로 고객의 개인정보를 수집할때 다른 개인정보와 별도로 동의 받지 않을 경우 법률위반 및 과징금 처분을 받을 수 있다.";
        item5.improvementGuides = "(1) 마케팅 목적으로 개인정보를 수집 이용하는 경우 다른 개인정보와 별도로 구분하여 동의 받도록 수정해야한다. (2) 마케팅을 목적으로 수집 이용에 대한 동의를 받을 때 동의 항목을 디폴트로 표시하지 않도록 수정해야한다.";
        item5.law = "개인정보 보호법 제22조제1항(동의를 받는 방법) 1천만원 이하의 과태료, 정보통신망 이용촉진 및 정보보호 등에 관한 법률 제50조제1항(영리목적의 광고성 정보 전송 제한) 3천만원 이하의 과태료";
        items.add(item5);

        // 항목 6
        EvaluationItem item6 = new EvaluationItem();
        item6.id = 6;
        item6.companyId = companyId;
        item6.area = "1. 개인정보처리단계(Lifecycle)";
        item6.field = "수집";
        item6.subField = "개인정보 수집의 적합성";
        item6.no = "1.1.6";
        item6.item = "고객의 사생활을 침해할 위험이 있는 민감정보를 수집할 경우 다른 개인정보와 별도로 동의 받거나, 법령의 근거하여 처리하고 있습니까?";
        item6.riskFactors = "고객의 민감정보를 다른 개인정보와 함께 수집할 경우 법률 위반 및 과징금 처분을 받을 수 있다.";
        item6.improvementGuides = "(1)  다른 법률에 근거해 민감정보를 수집할 경우 다른 개인정보와 분리해서 별도 동의를 받도록 수정해야한다.";
        item6.law = "개인정보 보호법 제23조제1항(민감정보의 처리 제한) 5년 이하의 징역 또는 5천만원 이하의 벌금, 개인정보 보호법 제23조제1항(민감정보의 처리 제한) 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item6);

        // 항목 7
        EvaluationItem item7 = new EvaluationItem();
        item7.id = 7;
        item7.companyId = companyId;
        item7.area = "1. 개인정보처리단계(Lifecycle)";
        item7.field = "수집";
        item7.subField = "개인정보 수집의 적합성";
        item7.no = "1.1.7";
        item7.item = "고객의 고유식별정보(주민등록번호 제외)를 수집할 경우 다른 개인정보와 별도로 동의 받거나, 법령에 근거하여 처리하고 있습니까?";
        item7.riskFactors = "고객의 고유식별정보를 다른 개인정보와 함께 수집할 경우 법률 위반 및 과징금 처분을 받을 수 있다.";
        item7.improvementGuides = "(1) 법률에 근거에 고유식별정보를 수집하는 경우 다른 개인정보 항목과 별도로 동의를 받아 수집하도록 수정해야한다.";
        item7.law = "개인정보 보호법 제24조제1항(고유식별정보의 처리 제한) 5년 이하의 징역 또는 5천만원 이하의 벌금, 개인정보 보호법 제24조제1항(고유식별정보의 처리 제한) 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item7);

        // 항목 8
        EvaluationItem item8 = new EvaluationItem();
        item8.id = 8;
        item8.companyId = companyId;
        item8.area = "1. 개인정보처리단계(Lifecycle)";
        item8.field = "수집";
        item8.subField = "개인정보 수집의 적합성";
        item8.no = "1.1.8";
        item8.item = "고객의 주민등록번호를 수집하는 경우 법적 근거가 있을 경우에만 수집하고 있으며, 주민등록번호를 이용하지 않고도 회원가입을 할 수 있게 계획하고 있습니까?";
        item8.riskFactors = "(1) 법률에서 정한 경우를 제외하고 고객의 주민등록번호를 처리할 경우 법률 위반 및 과징금 처분을 받을 수 있다. (2) 회원가입시 주민등록번호를 사용하지 않는 방법을 제공하지 않을 시 관련 법률 위반 및 과태료 처분을 받을 수 있다.";
        item8.improvementGuides = "(1) 고객이 주민등록번호를 사용하지 않고 회원가입을 할 수 있도록 구현해야한다.";
        item8.law = "개인정보 보호법 제24조의2(주민등록번호 처리의 제한) 전체 매출액의 100분의 3 이하의 과징금, 개인정보 보호법 제24조의2(주민등록번호 처리의 제한) 3천만원 이하의 과태료";
        items.add(item8);

        // 항목 9
        EvaluationItem item9 = new EvaluationItem();
        item9.id = 9;
        item9.companyId = companyId;
        item9.area = "1. 개인정보처리단계(Lifecycle)";
        item9.field = "수집";
        item9.subField = "개인정보 수집의 적합성";
        item9.no = "1.1.9";
        item9.item = "만 14세 미만 아동의 개인정보를 수집할 경우 법정대리의 동의를 받고 있습니까?";
        item9.riskFactors = "만 14세 미만 아동의 개인정보를 수집할때 법정대리인의 동의를 받지 않고 수집하는 경우 관련 법률 위반 및 과징금 처분을 받을 수 있다.";
        item9.improvementGuides = "(1) 만14세미만 아동의 개인정보를 수집할때는 법정대리인의 동의를 필수로 받을 수 있도록 구현해야한다. (2) 만14세미만 아동이 서비스를 이용할 수 없도록 가입자의 생년월일을 수집해 14세미만 아동이 가입할 수 없도록 사전에 방지해야한다.";
        item9.law = "개인정보 보호법 제22조의2의제1항(아동의 개인정보 보호) 전체 매출액의 100분의 3 이하의 과징금, 개인정보 보호법 제22조의2의제1항(아동의 개인정보 보호) 5년 이하의 징역 또는 5천만원 이하의 벌금";
        items.add(item9);

        // 항목 10
        EvaluationItem item10 = new EvaluationItem();
        item10.id = 10;
        item10.companyId = companyId;
        item10.area = "1. 개인정보처리단계(Lifecycle)";
        item10.field = "수집";
        item10.subField = "개인정보 수집의 적합성";
        item10.no = "1.1.10";
        item10.item = "고객의 동의없이 개인정보를 처리하는 경우 법령에 동의 없이 처리하는 예외사유에 해당합니까?";
        item10.riskFactors = "고객의 동의없이 개인정보를 처리하는 해당 내용을 개인정보처리방침에 기재하여 공개하지 않을 경우 법률 위반 및 과태료 처분을 받을 수 있다.";
        item10.improvementGuides = "(1) 고객의 동의없이 처리하는 개인정보에 대한 내용을 개인정보처리방침에 작성하여 확인 할 수 있게 한다.";
        item10.law = "개인정보 보호법 제22조제3항(동의를 받는 법) 1천만원 이하의 과태료";
        items.add(item10);

        // 항목 11
        EvaluationItem item11 = new EvaluationItem();
        item11.id = 11;
        item11.companyId = companyId;
        item11.area = "1. 개인정보처리단계(Lifecycle)";
        item11.field = "보유";
        item11.subField = "보유기간 산정";
        item11.no = "1.2.1";
        item11.item = "개인정보의 보유기간은 법령 기준 및 보유목적에 부합된 최소한의 기간으로 산정하고 있습니까?";
        item11.riskFactors = "개인정보의 보유기관을 명확히 산정하지 않을 경우 보유하고 있는 개인정보를 파악하지 못해 관리가 어렵다.";
        item11.improvementGuides = "(1) 고객의 개인정보에 대한 보유기간을 목적에 따라 명확하게 산정해야하며 이를 고객에게 고지해야한다.";
        item11.law = "개인정보 보호법 제21조제1항(개인정보의 파기) 3천만원 이하의 과태료";
        items.add(item11);

        // 항목 12
        EvaluationItem item12 = new EvaluationItem();
        item12.id = 12;
        item12.companyId = companyId;
        item12.area = "1. 개인정보처리단계(Lifecycle)";
        item12.field = "이용·제공";
        item12.subField = "제3자 제공";
        item12.no = "1.3.1";
        item12.item = "고객의 개인정보를 제3자에게 제공하는 경우 법령에 근거하여 제공하거나 정보주체의 별도 동의를 받아 제공하고 있습니까?";
        item12.riskFactors = "고객의 개인정보를 제3자 제공할 때 다른 법령의 근거하여 이용하지 않거나 별도의 동의를 받지않을 경우 고객의 권리를 침행할 수 있다.";
        item12.improvementGuides = "(1) 고객의 개인정보를 제3자제공할 경우 법령에 근거해 제공해야한다. (2) 만약 관련 법령이 없다면 고객에게 별도 동의를 받아 제3자 제공해야한다.";
        item12.law = "개인정보 보호법 제18조제2항(개인정보의 목적 외 이용ㆍ제공 제한) 전체 매출액의 100분의 3 이하의 과징금, 개인정보 보호법 제18조제2항(개인정보의 목적 외 이용ㆍ제공 제한) 5년 이하의 징역 또는 5천만원 이하의 벌금";
        items.add(item12);

        // 항목 13
        EvaluationItem item13 = new EvaluationItem();
        item13.id = 13;
        item13.companyId = companyId;
        item13.area = "1. 개인정보처리단계(Lifecycle)";
        item13.field = "이용·제공";
        item13.subField = "제3자 제공";
        item13.no = "1.3.2";
        item13.item = "고객의 동의를 받아 제3자 제공하는 경우 아래 항목을 고객에게 고지하고 있습니까?";
        item13.riskFactors = "고객의 동의를 받아 제3자 제공할 때 관련 내용을 고지하지 않을 경우 고객의 권리를 침해할 수 있다.";
        item13.improvementGuides = "(1) 제3자 제공을 위해 고객의 별도 동의를 받을 때 1.개인정보를 제공받는 자 2.개인정보를 제공받는 자의 개인정보 이용 목적 3. 제공하는 개인정보의 항목 4.개인정보를 제공받는 자의 개인정보 보유 및 이용 기간 5. 동의를 거부할 권리가 있다는 사실 및 동의 거부에 따른 불이익이 있는 경우에는 그 불이익의 내용을 다 작성한다. ";
        item13.law = "개인정보 보호법 제17조제2항(개인정보의 제공) 벌칙없음";
        items.add(item13);

        // 항목 14
        EvaluationItem item14 = new EvaluationItem();
        item14.id = 14;
        item14.companyId = companyId;
        item14.area = "1. 개인정보처리단계(Lifecycle)";
        item14.field = "이용·제공";
        item14.subField = "제3자 제공";
        item14.no = "1.3.3";
        item14.item = "고객의 정보를 제3자 제공하는 경우 관련 내용을 개인정보 처리방침에 공개하고 있습니까?";
        item14.riskFactors = "고객의 개인정보를 제3자 제공하는 경우 관련 내용을 개인정보처리방침에 공개하지 않을 경우 고객의 권리를 침해할 수 있다.";
        item14.improvementGuides = "제3자 제공할 경우 개인정보 처리방침에 관련 내용을 공개한다.";
        item14.law = "개인정보 보호법 제30조제1항(개인정보 처리방침의 수립 및 공개) 1천만원 이하의 과태료";
        items.add(item14);

        // 항목 15
        EvaluationItem item15 = new EvaluationItem();
        item15.id = 15;
        item15.companyId = companyId;
        item15.area = "1. 개인정보처리단계(Lifecycle)";
        item15.field = "국외 이전";
        item15.subField = "국외 이전";
        item15.no = "1.4.1";
        item15.item = "고객의 개인정보를 국외로 제공ㆍ처리위탁ㆍ보관(이하 이전)하는 경우 고객의 동의를 받거나 관련 법률에 따라 적법하게 이전하고 있는가?";
        item15.riskFactors = "개인정보를 고객의 동의나 관련 법률에 따라 적법하게 이전하지 않을 경우 법률 위반 및 과징금 처분을 받을 수 있다.";
        item15.improvementGuides = "(1) 법률의 근거 없이 고객의 개인정보를 국외로 이전하는 경우 필수적으로 고객의 동의를 받아야한다.  (2) 관련 법률의 근거해 국외이전하는 경우 관련 법률이 적법한지 검토해야한다.";
        item15.law = "개인정보 보호법 제28조의8제1항(개인정보의 국외 이전) 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item15);

        // 항목 16
        EvaluationItem item16 = new EvaluationItem();
        item16.id = 16;
        item16.companyId = companyId;
        item16.area = "1. 개인정보처리단계(Lifecycle)";
        item16.field = "국외 이전";
        item16.subField = "국외 이전";
        item16.no = "1.4.2";
        item16.item = "개인정보를 국외로 이전하는 경우 정보주체에게 국외이전에 관한 고지사항을 모두 알리고 있습니까?";
        item16.riskFactors = "고객의 동의를 받아 개인정보를 국외이전할때 관련 내용을 모두 고지하지 않을 경우 고객의 권리를 침해할 수 있다. ";
        item16.improvementGuides = "(1) 고객의 동의를 받아 국외이전하는 경우 1. 이전되는 개인정보 항목 2. 개인정보가 이전되는 국가, 시기 및 방법 3. 개인정보를 이전받는 자의 성명(법인인 경우에는 그 명칭과 연락처를 말한다) 4. 개인정보를 이전받는 자의 개인정보 이용목적 및 보유ㆍ이용 기간 5. 개인정보의 이전을 거부하는 방법, 절차 및 거부의 효과 를 빠짐없이 공개해야한다.";
        item16.law = "개인정보 보호법 제28조의8제2항(개인정보의 국외 이전) 벌칙 없음";
        items.add(item16);

        // 항목 17
        EvaluationItem item17 = new EvaluationItem();
        item17.id = 17;
        item17.companyId = companyId;
        item17.area = "1. 개인정보처리단계(Lifecycle)";
        item17.field = "국외 이전";
        item17.subField = "국외 이전";
        item17.no = "1.4.3";
        item17.item = "개인정보를 국외 이전하는 경우 개인정보처리방침에 국외 이전 내역을 공개하고 있습니까?";
        item17.riskFactors = "개인정보를 국외이전한다는 사실을 개인정보처리방침에 공개하지 않을 경우 고객의 권리를 침해할 수 있다.";
        item17.improvementGuides = "(1) 고객의 개인정보를 국외이전하는 경우 관련 내용을 개인정보처리방치에 공개해야 한다.";
        item17.law = "개인정보 보호법 제30조제1항(개인정보 처리방침의 수립 및 공개) 1천만원 이하의 과태료";
        items.add(item17);

        // 항목 18
        EvaluationItem item18 = new EvaluationItem();
        item18.id = 18;
        item18.companyId = companyId;
        item18.area = "1. 개인정보처리단계(Lifecycle)";
        item18.field = "위탁";
        item18.subField = "위탁";
        item18.no = "1.5.1";
        item18.item = "개인정보 처리에 관한 업무를 위탁하는 경우 업무 내용과 *수탁자(*재수탁자 포함)에 대한 사항을 고객에게 공개하고 있습니까?";
        item18.riskFactors = "개인정보의 업무를 위탁하는 경우 관련 사항을 고객에게 공개·통지하지 않을 경우 고객의 권리를 제한할 수 있다. ";
        item18.improvementGuides = "(1) 개인정보처리 업무를 위탁하는 경우 위탁하는 업무 내용을 고객에게 고지해야한다. (2) 수탁자(재수탁자)에 대한 내용 역시 고객에게 공개해야한다.";
        item18.law = "개인정보 보호법 제26조제2항(업무위탁에 따른 개인정보의 처리 제한) 1천만원 이하의 과태료";
        items.add(item18);

        // 항목 19
        EvaluationItem item19 = new EvaluationItem();
        item19.id = 19;
        item19.companyId = companyId;
        item19.area = "1. 개인정보처리단계(Lifecycle)";
        item19.field = "위탁";
        item19.subField = "수탁사 관리·감독";
        item19.no = "1.5.2";
        item19.item = "개인정보 처리에 관한 업무를 위탁받아 처리하는 자가 위탁받은 개인정보 처리 업무를 제3자에게 다시 위탁하려는 경우에는 위탁자의 동의를 받도록 계획하고 있습니까?";
        item19.riskFactors = "위탁업무를 다시 제3자에게 위탁할때 동의를 받지 않을 경우 위탁자에 대한 관리·감독이 어려울 수 있다.";
        item19.improvementGuides = "(1)수탁자가 위탁받은 개인정보 처리 업무를 다른 3자에게 재위탁하는 경우 위탁자에게 동의를 받아야한다.";
        item19.law = "개인정보 보호법 제26조제6항(업무위탁에 따른 개인정보의 처리 제한) 2천만원 이하의 과태료";
        items.add(item19);

        // 항목 20
        EvaluationItem item20 = new EvaluationItem();
        item20.id = 20;
        item20.companyId = companyId;
        item20.area = "1. 개인정보처리단계(Lifecycle)";
        item20.field = "파기";
        item20.subField = "파기 계획 수립";
        item20.no = "1.6.1";
        item20.item = "회원탈퇴나 동의 철회 시 개인정보를 파기하도록 구현하고 있습니까?";
        item20.riskFactors = "회원탈퇴, 동의 철회시 해당 고객의 개인정보를 파기하지 않을 경우 법률 위반 및 과태료 처분을 받을 수 있다.";
        item20.improvementGuides = "(1) 고객이 회원탈퇴 또는 동의 철회를 할 경우 해당 개인정보를 지체없이 파기해야하며 법률에 의해 보관해야하는 내용이 있을 경우 관련 법률에따라 일정기간 분리보관해야한다.";
        item20.law = "개인정보 보호법 제37조제3항(개인정보의 파기) 3천만원 이하의 과태료";
        items.add(item20);

        // 항목 21
        EvaluationItem item21 = new EvaluationItem();
        item21.id = 21;
        item21.companyId = companyId;
        item21.area = "1. 개인정보처리단계(Lifecycle)";
        item21.field = "파기";
        item21.subField = "파기 계획 수립";
        item21.no = "1.6.2";
        item21.item = "고객에게 고지한 기간이 만료될 경우 지체 없이 파기하도록 구현하였습니까?";
        item21.riskFactors = "보유기간이 경과된 개인정보를 파기하지 않을 경우 법률 위반 및 과태료 처분을 받을 수 있다.";
        item21.improvementGuides = "(1) 고객에게 고지한 기간이 지났을 경우 개인정보를 지체없이 파기해야하며 법률에 의해 보관해야하는 내용이 있을 경우 관련 법률에따라 일정기간 분리보관해야한다.";
        item21.law = "개인정보 보호법 제21조제1항(개인정보의 파기) 3천만원 이하의 과태료";
        items.add(item21);

        // 항목 22
        EvaluationItem item22 = new EvaluationItem();
        item22.id = 22;
        item22.companyId = companyId;
        item22.area = "1. 개인정보처리단계(Lifecycle)";
        item22.field = "파기";
        item22.subField = "분리보관 계획 수립";
        item22.no = "1.6.3";
        item22.item = "이용목적 달성시(회원탈퇴, 동의 철회) 다른 법령에 따라 보존해야 하는 경우 해당 개인정보 또는 개인정보파일을 다른 개인정보와 분리하여 저장·관리하도록 계획하고 있습니까?";
        item22.riskFactors = "다른 법령에 따란 보존해야하는 개인정보를 분리 하여 저장·관리하지 않을 경우 관련 법률 위반 및 과태료 처분을 받을 수 있다.";
        item22.improvementGuides = "(1) 이용목적을 달성한 개인정보 중 관련 법률에 의해 보관해야할 경우 다른 개인정보와 분리보관해야한다.";
        item22.law = "개인정보 보호법 제21조제3항(개인정보의 파기) 1천만원 이하의 과태료";
        items.add(item22);

        // 항목 23
        EvaluationItem item23 = new EvaluationItem();
        item23.id = 23;
        item23.companyId = companyId;
        item23.area = "1. 개인정보처리단계(Lifecycle)";
        item23.field = "파기";
        item23.subField = "분리보관 계획 수립";
        item23.no = "1.6.4";
        item23.item = "법령에 근거하여 개인정보 또는 개인정보 파일을 분리 저장·관리한다는 사실을 개인정보 처리방침을 통해 공개하고 있습니까?";
        item23.riskFactors = "개인정보를 파기하지 않고 분리보관한다는 사실을 개인정보처리방침에 공개하지 않을 경우 고객의 권리를 침해할 수 있다. ";
        item23.improvementGuides = "(1) 관련 법령에 의해서 개인정보를 파기하지않고 분리보관할 경우 해당 내용을 개인정보처리방침에 공개해야한다.";
        item23.law = "개인정보 보호법 제30조제1항(개인정보 처리방침의 수립 및 공개) 1천만원 이하의 과태료";
        items.add(item23);

        // 항목 24
        EvaluationItem item24 = new EvaluationItem();
        item24.id = 24;
        item24.companyId = companyId;
        item24.area = "2. 개인정보처리시스템(Admin)";
        item24.field = "접근권한 관리";
        item24.subField = "계정 관리";
        item24.no = "2.1.1";
        item24.item = "개인정보 취급자의 책임 추적성을 확보하기 위하여 유일한 식별자를 사용하고 있습니까?";
        item24.riskFactors = "개인정보취급자별로 유일한 식별자(ID)가 부여되지 않을 경우 개인정보 처리 행위에 대한 책임추적성을 확보할 수 없으며, 개인정보 오·남용 또는 침해사고 발생 시 실제 처리자를 특정할 수 없어 책임소재를 명확히 파악하기 어렵다.";
        item24.improvementGuides = "개인정보취급자별로 유일하게 구분할 수 있는 고유한 식별자(사용자 계정, ID)를 할당하고, 정당한 사유가 없는 한 다른 개인정보취급자와 계정을 공유하지 않도록 하여 책임추적성을 확보한다. 관리자 및 특수권한 계정은 쉽게 추측 가능한 식별자(root, admin, administrator 등) 사용을 제한하고, 시스템 설치 후 제조사·판매사의 기본계정 및 시험계정은 제거하거나 추측이 어려운 계정으로 변경한다.";
        item24.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item24);

        // 항목 25
        EvaluationItem item25 = new EvaluationItem();
        item25.id = 25;
        item25.companyId = companyId;
        item25.area = "2. 개인정보처리시스템(Admin)";
        item25.field = "접근권한 관리";
        item25.subField = "계정 관리";
        item25.no = "2.1.2";
        item25.item = "공유 계정 사용 허용에 대한 내부 승인 절차를 준수하고 있습니까?";
        item25.riskFactors = "정당한 사유 및 승인 없이 공유계정을 사용하는 경우 특정 개인의 업무처리 내역 추적이 불가능하여 실제 업무 수행자를 식별할 수 없고 책임소재 파악 및 비인가 접근 위험이 증가한다.";
        item25.improvementGuides = "공유계정 사용은 원칙적으로 금지하되, 공유계정 사용이 불가피한 경우 공유 사유, 사용자 범위, 사용 기간을 명확히 정의하고 책임자 승인을 받아 사용하며, 책임추적성 보장을 위한 추가 통제방안(계정 관리 대장 작성, 접근제어시스템 도입, 사용자 계정으로 로그인 후 공유계정으로 전환, 2차 인증 적용 등)을 마련한다. 유지보수 업무 등으로 임시적으로 계정을 공유한 경우 업무 종료 후 즉시 비밀번호를 변경한다.";
        item25.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item25);

        // 항목 26
        EvaluationItem item26 = new EvaluationItem();
        item26.id = 26;
        item26.companyId = companyId;
        item26.area = "2. 개인정보처리시스템(Admin)";
        item26.field = "접근권한 관리";
        item26.subField = "인증 관리";
        item26.no = "2.1.3";
        item26.item = "사용자의 비밀번호는 보안성을 준수하고 있습니까?";
        item26.riskFactors = "비밀번호의 복잡도가 낮거나 추측 가능한 비밀번호를 사용할 경우 무단 접근 및 계정 도용 위험이 증가하여 개인정보 유출 가능성이 높아진다.";
        item26.improvementGuides = "비밀번호 정책을 수립하여 영문 대소문자, 숫자, 특수문자 중 2종류 이상 조합 10자리 이상 또는 3종류 이상 조합 8자리 이상으로 설정하고, 생일·전화번호·계정명 등 추측 가능한 비밀번호 사용을 시스템적으로 제한한다. 연속된 문자(1234, abcd 등)나 동일 문자 반복(1111, aaaa 등)도 차단하며, 비밀번호 복잡도 검증 모듈을 적용하여 설정 시점에 안전성을 확인한다.";
        item26.law = "개인정보 보호법 제29조(안전조치의무) 3천만 원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item26);

        // 항목 27
        EvaluationItem item27 = new EvaluationItem();
        item27.id = 27;
        item27.companyId = companyId;
        item27.area = "2. 개인정보처리시스템(Admin)";
        item27.field = "접근권한 관리";
        item27.subField = "인증 관리";
        item27.no = "2.1.4";
        item27.item = "비밀번호를 정기적으로 변경하도록 제안하고 있습니까?";
        item27.riskFactors = "비밀번호를 장기간 사용할 경우 유출 위험이 증가하며, 정기적으로 변경하지 않으면 이전에 유출된 비밀번호를 통한 무단 접근 가능성이 지속된다.";
        item27.improvementGuides = "비밀번호 정기 변경 주기를 설정(예: 6개월 또는 1년)하고, 변경 시 이전 비밀번호 재사용을 제한(최근 3~5개)하며, 비밀번호 변경 알림 기능을 통해 개인정보취급자에게 변경 시기를 사전에 안내한다. 관리자 계정의 경우 더 짧은 주기(3개월 등)로 변경하도록 설정하고, 변경 이력을 기록·관리한다.";
        item27.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item27);

        // 항목 28
        EvaluationItem item28 = new EvaluationItem();
        item28.id = 28;
        item28.companyId = companyId;
        item28.area = "2. 개인정보처리시스템(Admin)";
        item28.field = "접근권한 관리";
        item28.subField = "인증 관리";
        item28.no = "2.1.5";
        item28.item = "일정 횟수 이상 인증에 실패한 경우 개인정보처리시스템에 대한 접근을 제한하고 있습니까?";
        item28.riskFactors = "인증 실패 횟수에 대한 제한이 없을 경우 무차별 대입 공격(Brute Force Attack)을 통한 계정 탈취 위험이 증가하여 개인정보 무단 접근이 발생할 수 있다.";
        item28.improvementGuides = "로그인 실패 횟수를 일정 횟수(예: 5회)로 제한하고, 해당 횟수 초과 시 계정을 자동으로 잠금 처리하며, 계정 잠금 해제는 관리자 승인 또는 본인 인증(SMS, 이메일 인증 등) 절차를 통해서만 가능하도록 설정한다. 무차별 대입 공격(Brute Force Attack) 탐지 및 차단 시스템을 도입하고, 짧은 시간 내 반복적인 로그인 실패 시 해당 IP 주소를 일시적으로 차단한다.";
        item28.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item28);

        // 항목 29
        EvaluationItem item29 = new EvaluationItem();
        item29.id = 29;
        item29.companyId = companyId;
        item29.area = "2. 개인정보처리시스템(Admin)";
        item29.field = "접근권한 관리";
        item29.subField = "권한 관리";
        item29.no = "2.1.6";
        item29.item = "개인정보 취급자의 인사 이동 등 업무 변경 시 접근 권한을 제한하고 있습니까?";
        item29.riskFactors = "인사이동 등으로 업무가 변경되었음에도 접근권한이 유지될 경우 불필요한 개인정보 접근이 가능하여 내부자에 의한 개인정보 유출 위험이 증가한다.";
        item29.improvementGuides = "인사이동, 퇴직, 휴직 등 업무 변경 발생 시 즉시 개인정보처리시스템의 접근권한을 변경 또는 말소하는 절차를 수립하고, 인사 담당 부서와 개인정보 관리 부서 간 협조 체계를 구축하여 권한 변경을 지체 없이 처리한다. 퇴직 점검표에 사용자 계정 말소 항목을 반영하고, 다수 시스템 사용 시 모든 시스템의 접근 권한이 완전히 변경·말소되었는지 확인하는 절차를 마련한다.";
        item29.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item29);

        // 항목 30
        EvaluationItem item30 = new EvaluationItem();
        item30.id = 30;
        item30.companyId = companyId;
        item30.area = "2. 개인정보처리시스템(Admin)";
        item30.field = "접근권한 관리";
        item30.subField = "권한 관리";
        item30.no = "2.1.7";
        item30.item = "개인정보처리시스템의 접근권한을 부여, 변경, 말소한 내역을 기록하고 최소 3년간 보관하도록 계획하고 있습니까?";
        item30.riskFactors = "접근권한 변경 이력을 기록·관리하지 않을 경우 비인가 권한 부여 또는 권한 남용을 사후에 확인할 수 없어 개인정보 침해사고 발생 시 원인 파악 및 책임 추적이 어렵다.";
        item30.improvementGuides = "접근권한의 부여·변경·말소 내역을 전자적으로 기록·관리하는 시스템을 구축하고, 기록 항목에는 신청자, 승인자, 신청일시, 승인일시, 권한 내용, 신청 및 발급 사유 등을 포함하며, 최소 3년 이상 보관한다. 접근권한 관리 이력에 대한 정기적인 검토를 통해 불필요한 권한 보유 여부를 확인하고 즉시 조치한다.";
        item30.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item30);

        // 항목 31
        EvaluationItem item31 = new EvaluationItem();
        item31.id = 31;
        item31.companyId = companyId;
        item31.area = "2. 개인정보처리시스템(Admin)";
        item31.field = "접근통제";
        item31.subField = "접근통제 조치";
        item31.no = "2.2.1";
        item31.item = "개인정보취급자가 일정 시간 이상 업무처리를 하지 않는 경우 자동으로 시스템 접속을 차단하고 있습니까?";
        item31.riskFactors = "세션 타임아웃이 설정되지 않을 경우 방치된 시스템을 통해 제3자가 무단으로 개인정보에 접근할 수 있어 개인정보 유출 위험이 증가한다.";
        item31.improvementGuides = "일정 시간(예: 10분 또는 30분) 동안 사용자의 활동이 없을 경우 자동으로 세션을 종료하고 재접속 시 재인증을 요구하도록 세션 타임아웃을 설정한다. 업무 특성에 따라 적절한 시간을 차등 설정하되, 개인정보 다운로드·파기·권한 설정이 가능한 관리자 계정은 더 짧은 시간(10분 이내)으로 설정한다. 세션 타임아웃 후 재접속 시 초기 로그인과 동일한 수준의 인증 절차를 적용한다.";
        item31.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item31);

        // 항목 32
        EvaluationItem item32 = new EvaluationItem();
        item32.id = 32;
        item32.companyId = companyId;
        item32.area = "2. 개인정보처리시스템(Admin)";
        item32.field = "접근통제";
        item32.subField = "접근통제 조치";
        item32.no = "2.2.2";
        item32.item = "개인정보처리시스템에 대한 정당한 접근 권한을 가진 자(정보주체 제외)가 정보통신망을 통해 외부에서 개인정보처리시스템에 접속하려는 경우 인증서, 보안토큰, 일회용 비밀번호, 생체인식 등 안전한 인증수단을 적용하고 있습니까?";
        item32.riskFactors = "외부에서 개인정보처리시스템에 접속 시 강화된 인증수단을 적용하지 않을 경우 네트워크 도청, 계정 탈취 등을 통한 비인가 접근으로 개인정보 유출 위험이 크게 증가한다.";
        item32.improvementGuides = "외부에서 개인정보처리시스템 접속 시 공인인증서, OTP(일회용 비밀번호), 보안토큰 등 2요소 이상의 인증수단(2FA)을 적용하고, VPN(가상사설망) 등 안전한 접속 수단을 통해서만 접근이 가능하도록 설정한다. IPsec, SSL/TLS 기반의 암호화된 터널링 기술을 사용하고, VPN 접속 로그를 별도로 기록·관리하며, 해외 IP나 비인가 지역에서의 접속 시도는 추가 인증이나 차단 조치를 적용한다.";
        item32.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item32);

        // 항목 33
        EvaluationItem item33 = new EvaluationItem();
        item33.id = 33;
        item33.companyId = companyId;
        item33.area = "2. 개인정보처리시스템(Admin)";
        item33.field = "접근통제";
        item33.subField = "접근통제 조치";
        item33.no = "2.2.3";
        item33.item = "개인정보처리시스템에서 개인정보를 다운로드 또는 파기할 수 있거나 접근권한을 설정할 수 있는 개인정보취급자의 컴퓨터 등에 대한 인터넷망 차단 조치를 하고 있습니까?";
        item33.riskFactors = "개인정보 다운로드 또는 접근권한 설정이 가능한 컴퓨터에 인터넷망 차단 조치를 하지 않을 경우 악성코드 감염, 외부 공격 등을 통한 대량 개인정보 유출 위험이 증가한다.";
        item33.improvementGuides = "개인정보 다운로드, 파기, 접근권한 설정이 가능한 관리자 컴퓨터는 물리적 또는 논리적 인터넷 망분리를 실시하거나, 불가피한 경우 화이트리스트 기반의 웹사이트 접근 제한(업무 필수 사이트만 허용) 및 USB 등 외부 저장매체 차단 조치를 적용한다. 망분리 적용이 어려운 경우 애플리케이션 가상화(Application Virtualization), 가상 데스크톱(VDI) 등 논리적 망분리 기술을 활용하고, 클라우드 서비스 이용 시 해당 서비스 접속 외에는 인터넷을 차단한다.";
        item33.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item33);

        // 항목 34
        EvaluationItem item34 = new EvaluationItem();
        item34.id = 34;
        item34.companyId = companyId;
        item34.area = "2. 개인정보처리시스템(Admin)";
        item34.field = "개인정보의 암호화";
        item34.subField = "저장 시 암호화";
        item34.no = "2.3.1";
        item34.item = "중요한 개인정보를 저장하는 경우, 안전한 방식으로 암호화하고 있습니까?";
        item34.riskFactors = "중요한 개인정보를 암호화하지 않고 저장할 경우 데이터베이스 침해 또는 저장매체 유출 시 개인정보가 그대로 노출되어 정보주체에게 심각한 피해를 초래할 수 있다.";
        item34.improvementGuides = "이용자의 주민등록번호, 여권번호, 운전면허번호, 외국인등록번호, 신용카드번호, 계좌번호, 생체인식정보는 안전한 암호 알고리즘(AES-256, ARIA-256 등)으로 암호화하여 저장한다. 이용자가 아닌 정보주체의 경우 인터넷망 구간 및 DMZ에 고유식별정보를 저장할 때는 반드시 암호화하고, 내부망 저장 시에도 주민등록번호는 필수적으로 암호화하되 그 외 고유식별정보는 영향평가 결과 또는 위험도 분석에 따라 암호화 적용 여부를 결정한다.";
        item34.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item34);

        // 항목 35
        EvaluationItem item35 = new EvaluationItem();
        item35.id = 35;
        item35.companyId = companyId;
        item35.area = "2. 개인정보처리시스템(Admin)";
        item35.field = "개인정보의 암호화";
        item35.subField = "저장 시 암호화";
        item35.no = "2.3.2";
        item35.item = "비밀번호를 저장하는 경우 일방향 암호화를 하고 있습니까?";
        item35.riskFactors = "비밀번호를 일방향 암호화하지 않고 평문 또는 복호화 가능한 방식으로 저장할 경우 데이터베이스 유출 시 모든 사용자의 비밀번호가 노출되어 다른 서비스의 계정까지 도용당할 위험이 있다.";
        item35.improvementGuides = "비밀번호는 SHA-256 이상의 일방향 해시함수를 사용하여 암호화하고, salt 값을 추가하여 rainbow table 공격을 방지하며, 복호화가 불가능하도록 일방향 암호화만 적용한다. 해시 함수 적용 시 충분한 반복 횟수(iteration)를 설정하여 무차별 대입 공격에 대한 저항력을 강화하고, bcrypt, PBKDF2, Argon2 등 검증된 비밀번호 해싱 알고리즘 사용을 권장한다.";
        item35.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item35);

        // 항목 36
        EvaluationItem item36 = new EvaluationItem();
        item36.id = 36;
        item36.companyId = companyId;
        item36.area = "2. 개인정보처리시스템(Admin)";
        item36.field = "개인정보의 암호화";
        item36.subField = "저장 시 암호화";
        item36.no = "2.3.3";
        item36.item = "개인정보를 암호화하는 경우 안전한 알고리즘을 적용하고 있습니까?";
        item36.riskFactors = "취약한 암호 알고리즘을 사용할 경우 암호화된 개인정보가 쉽게 복호화될 수 있어 암호화의 실효성이 없으며, 개인정보 유출 시 정보주체 보호가 불가능하다.";
        item36.improvementGuides = "국가정보원(KCMVP), NIST, CRYPTREC 등 공인된 기관에서 안전하다고 인정한 암호 알고리즘(AES-256, ARIA-256, SHA-256 이상 등)을 사용하고, DES, 3DES, MD5, SHA-1 등 취약한 알고리즘 사용을 금지하며, 암호 알고리즘을 주기적으로 검토하여 최신 보안 기준을 유지한다. 암호화 라이브러리는 검증된 공개 라이브러리(OpenSSL, BouncyCastle 등) 사용을 권장하고, 자체 개발 암호화 모듈은 지양한다.";
        item36.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item36);

        // 항목 37
        EvaluationItem item37 = new EvaluationItem();
        item37.id = 37;
        item37.companyId = companyId;
        item37.area = "2. 개인정보처리시스템(Admin)";
        item37.field = "개인정보의 암호화";
        item37.subField = "저장 시 암호화";
        item37.no = "2.3.4";
        item37.item = "암호화된 개인정보를 안전하게 보관하기 위하여 안전한 암호키 생성, 이용, 보관, 배포 및 파기 등에 관한 절차를 수립·시행하고 있습니까?";
        item37.riskFactors = "암호키 관리 절차가 수립되지 않아 암호키가 평문으로 소스코드에 하드코딩되거나 접근 가능한 위치에 저장될 경우, 암호키 유출로 암호화된 모든 개인정보가 노출될 수 있다.";
        item37.improvementGuides = "암호키는 소스코드에 하드코딩하지 않고 별도의 안전한 저장소(HSM, Key Management System, AWS KMS, Azure Key Vault 등)에 암호화하여 보관하고, 암호키 접근 권한을 최소한의 인원으로 제한하며, 암호키 생성·이용·변경·파기 등 전체 생명주기에 대한 절차를 문서화하여 관리한다. 암호키 백업 및 복구 절차를 수립하고, 암호키 유효기간을 설정하여 주기적으로 갱신하며, 암호키 접근 이력을 별도로 기록·관리한다.";
        item37.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item37);

        // 항목 38
        EvaluationItem item38 = new EvaluationItem();
        item38.id = 38;
        item38.companyId = companyId;
        item38.area = "2. 개인정보처리시스템(Admin)";
        item38.field = "개인정보의 암호화";
        item38.subField = "전송 시 암호화";
        item38.no = "2.3.5";
        item38.item = "개인정보를 전송하는 경우 암호화를 적용하고 있습니까?";
        item38.riskFactors = "개인정보 전송 시 암호화를 적용하지 않을 경우 네트워크 구간에서 개인정보가 평문으로 노출되어 스니핑(Sniffing) 등의 공격을 통해 개인정보가 유출될 수 있다.";
        item38.improvementGuides = "정보통신망을 통해 비밀번호, 생체인식정보 등 인증정보를 전송하는 경우 반드시 암호화를 적용하고, 인터넷 구간을 통해 개인정보를 전송하는 경우 SSL/TLS(TLS 1.2 이상) 등 안전한 암호화 프로토콜을 적용하여 전 구간 암호화 통신을 구현한다. 내부망에서도 중요 개인정보 전송 시 암호화를 적용하고, HTTPS 강제 적용(HSTS), 최신 암호화 스위트(Cipher Suite) 사용, 안전하지 않은 프로토콜(SSLv2, SSLv3, TLS 1.0/1.1) 비활성화 등의 조치를 취한다.";
        item38.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item38);

        // 항목 39
        EvaluationItem item39 = new EvaluationItem();
        item39.id = 39;
        item39.companyId = companyId;
        item39.area = "2. 개인정보처리시스템(Admin)";
        item39.field = "접속기록의 보관 및 점검";
        item39.subField = "접속기록 보관";
        item39.no = "2.4.1";
        item39.item = "접속기록을 저장할때 식별자(ID), 접속일시, 접속지(IP), 처리한 정보주체 정보, 수행업무를 기록하고 있습니까?";
        item39.riskFactors = "접속기록에 필수 정보가 누락되거나 보관기간이 부족할 경우 개인정보 침해사고 발생 시 원인 분석 및 책임 추적이 불가능하여 재발 방지 대책 수립이 어렵다.";
        item39.improvementGuides = "개인정보처리시스템 접속 시 사용자 ID, 접속일시(년-월-일 시:분:초), 접속지 IP 주소, 처리한 정보주체 정보(정보주체의 ID, 고객번호 등), 수행업무(검색, 조회, 입력, 수정, 삭제, 출력, 다운로드 등) 등 5가지 필수 항목을 모두 자동으로 기록한다. 5만명 이상 또는 고유식별정보·민감정보 처리 시스템은 2년 이상, 그 외는 1년 이상 보관하며, 책임 추적성 확보를 위해 보관기간 연장을 권장한다.";
        item39.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item39);

        // 항목 40
        EvaluationItem item40 = new EvaluationItem();
        item40.id = 40;
        item40.companyId = companyId;
        item40.area = "2. 개인정보처리시스템(Admin)";
        item40.field = "접속기록의 보관 및 점검";
        item40.subField = "접속기록 보관";
        item40.no = "2.4.2";
        item40.item = "정보시스템의 로그기록은 위∙변조 및 도난, 분실되지 않도록 별도로 분리 보관하고 있습니까?";
        item40.riskFactors = "접속기록을 별도로 분리 보관하지 않을 경우 침해사고 발생 시 공격자가 접속기록을 삭제·변조하여 증거 인멸이 가능하며, 사고 원인 파악 및 법적 대응이 불가능하다.";
        item40.improvementGuides = "접속기록은 업무용 시스템과 물리적 또는 논리적으로 분리된 별도의 로그 서버 또는 WORM(Write Once Read Many) 등 덮어쓰기 방지 매체에 저장하고, 로그 파일에 대한 접근 권한을 최소한으로 제한하며, 로그의 무결성 검증을 위한 해시값(MAC, 전자서명 등)을 생성하여 별도 위치에 관리한다. 로그 수집·저장·분석 시스템(SIEM, ESM 등)을 활용하여 중앙 집중식으로 관리하고, 로그 위·변조 탐지 및 알림 기능을 적용한다.";
        item40.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item40);

        // 항목 41
        EvaluationItem item41 = new EvaluationItem();
        item41.id = 41;
        item41.companyId = companyId;
        item41.area = "2. 개인정보처리시스템(Admin)";
        item41.field = "접속기록의 보관 및 점검";
        item41.subField = "접속기록 보관";
        item41.no = "2.4.3";
        item41.item = "개인정보의 오·남용, 분실·도난, 유출·위조·변조 또는 훼손 등에 대응하기 위하여 개인정보취급자의 개인정보처리시스템에 대한 접속기록 및 개인정보 다운로드 상황을 확인하고 점검하는 주기ㆍ방법ㆍ사후조치절차 등을 내부 관리계획으로 정하고 이행하고 있습니까?";
        item41.riskFactors = "접속기록을 정기적으로 점검하지 않을 경우 비정상적인 접근, 대량 다운로드, 근무시간 외 접속 등 개인정보 침해 징후를 조기에 발견하지 못하여 피해가 확대될 수 있다.";
        item41.improvementGuides = "월 1회 이상 접속기록을 점검하고, 비인가 접근, 근무시간 외 접속, 동일 사용자의 과도한 조회·다운로드, 해외 IP나 비인가 지역 접속, 대량 개인정보 처리, 계정 공유 의심 행위(짧은 시간 내 여러 IP에서 동일 계정 접속) 등 이상 징후를 탐지하며, 점검 결과를 문서화하고 이상 발견 시 즉시 조치(권한 정지, 조사, 보고 등)한다. 자동화된 로그 분석 시스템을 활용하여 실시간 이상 행위 탐지 및 알림 기능을 구현하고, 특히 개인정보 다운로드 발생 시 사유를 반드시 확인한다.";
        item41.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item41);

        // 항목 42
        EvaluationItem item42 = new EvaluationItem();
        item42.id = 42;
        item42.companyId = companyId;
        item42.area = "2. 개인정보처리시스템(Admin)";
        item42.field = "기타 기술적 보호조치";
        item42.subField = "출력 시 보호조치";
        item42.no = "2.5.1";
        item42.item = "개인정보처리시스템에서 개인정보의 출력(인쇄, 화면 표시, 파일생성 등)시 특정한 용도에 따라 출력항목을 최소화하여 출력하고 있습니까?";
        item42.riskFactors = "개인정보 출력 시 용도와 관계없이 모든 항목을 출력할 경우 불필요한 개인정보 노출로 인한 유출 위험이 증가하며, 최소한의 정보만 처리하는 원칙에 위배된다.";
        item42.improvementGuides = "개인정보 출력 시 출력 용도를 선택(고객 상담용, 계약서 출력용, 통계 분석용 등)하도록 하고, 용도에 따라 필요한 최소한의 항목만 출력되도록 시스템을 설계하며, 불필요한 개인정보는 마스킹 처리(주민등록번호 뒷자리 ****** 처리, 전화번호 뒷자리 **** 처리 등)하여 출력한다. 동일한 정보주체에 대해 여러 시스템에서 서로 다른 마스킹 방식을 적용하면 조합하여 식별 가능하므로 조직 전체에 통일된 마스킹 정책을 적용하고, 출력 이력을 기록·관리한다.";
        item42.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item42);

        // 항목 43
        EvaluationItem item43 = new EvaluationItem();
        item43.id = 43;
        item43.companyId = companyId;
        item43.area = "2. 개인정보처리시스템(Admin)";
        item43.field = "기타 기술적 보호조치";
        item43.subField = "출력 시 보호조치";
        item43.no = "2.5.2";
        item43.item = "개인정보 검색 시 LIKE 검색을 제한하고 있습니까?";
        item43.riskFactors = "개인정보 검색 시 부분검색(like 검색)을 무제한 허용할 경우 개인정보취급자가 업무 목적 없이 불특정 다수의 개인정보를 무단으로 조회·수집할 수 있어 개인정보 오·남용 위험이 증가한다.";
        item43.improvementGuides = "개인정보 검색 시 like 검색(부분 검색, 예: WHERE name LIKE '%김%')은 업무상 필수적인 경우에만 제한적으로 허용하고, 원칙적으로 완전 일치 검색(equal 검색, 예: WHERE name = '김철수')을 사용하도록 제한한다. 2개 이상의 검색 조건을 조합하여 입력하도록 제한(예: 이름 + 생년월일, 전화번호 + 주소 등)하고, 상담센터 등 예외적으로 부분 검색이 필요한 경우 별도 승인 절차를 거쳐 허용하며, like 검색 사용 시 검색 조건과 결과를 접속기록에 상세히 기록한다. 와일드카드 검색을 완전히 허용하는 경우(LIKE '%%') 무분별한 전체 조회가 가능하므로 이를 시스템적으로 차단하고, 검색 결과 건수를 제한(예: 최대 100건)하는 정책을 적용한다.";
        item43.law = "개인정보 보호법 제29조(안전조치의무) 3천만원 이하의 과태료, 안전성 확보조치를 다하지 않은 경우 개인정보 분실ㆍ도난ㆍ유출ㆍ위조ㆍ변조ㆍ훼손 시 전체 매출액의 100분의 3 이하의 과징금";
        items.add(item43);

        // 항목 44
        EvaluationItem item44 = new EvaluationItem();
        item44.id = 44;
        item44.companyId = companyId;
        item44.area = "3. 보안성 검토";
        item44.field = "비밀번호 기술적 보호조치";
        item44.subField = "자동완성 방지";
        item44.no = "3.1.1.1";
        item44.item = "텍스트를 입력할 때 자동완성을 비활성화 하여 비밀번호가 자동완성 되지 않도록 계획하고 있습니까?";
        item44.riskFactors = "메모리나 디스크에서 처리하는 중요데이터(개인정보, 인증정보, 금융정보)가 제대로 보호되지 않을 경우, 보안이나 데이터의 무결성이 훼손될 수 있다. 특히 프로그램이 개인정보, 인증정보 등의 사용자 중요정보 및 시스템 중요정보를 처리하는 과정에서 이를 평문으로 저장할 경우 공격자에게 민감한 정보가 노출될 수 있다.";
        item44.improvementGuides = "(1) 인증정보와 같은 민감한 정보를 포함하는 웹 폼을 구현하는 경우 자동완성 기능을 비활성화 하도록 시큐어코딩 규칙을 정의한다. (2) 민감한 정보를 포함하는 페이지는 사용자 측 캐싱을 비활성화 하도록 제한적인 캐시정책을 수립하여야 하며, 부득이 캐싱을 해야 하는 경우 캐싱되는 정보는 암호화하여 저장하도록 설계한다.";
        item44.law = "관련 법률 없음";
        items.add(item44);

        // 항목 45
        EvaluationItem item45 = new EvaluationItem();
        item45.id = 45;
        item45.companyId = companyId;
        item45.area = "3. 보안성 검토";
        item45.field = "비밀번호 기술적 보호조치";
        item45.subField = "임시 비밀번호";
        item45.no = "3.1.2.1";
        item45.item = "임시 비밀번호를 정보주체에게 발급할 경우, 난수값을 사용하고 정보주체가 회원가입 시 등록한 E-mail, 휴대폰 문자 등으로 전달하도록 계획하고 있습니까?";
        item45.riskFactors = "예측 가능한 난수를 사용하는 것은 시스템의 보안약점을 유발한다. 예측 불가능한 숫자가 필요한 상황에서 예측 가능한 난수를 사용한다면, 공격자는 SW에서 생성되는 다음 숫자를 예상하여 시스템을 공격하는 것이 가능하다.";
        item45.improvementGuides = "(1) 비밀번호를 변경할 시에는 아이핀, 휴대폰 본인인증 등 인증수단을 통해 개인정보취급자 본인임을 인증하는 절차를 수행해야 한다. (2) 난수 생성 시 안전한 난수 생성 알고리즘을 사용해야 한다.";
        item45.law = "관련 법률 없음";
        items.add(item45);

        // 항목 46
        EvaluationItem item46 = new EvaluationItem();
        item46.id = 46;
        item46.companyId = companyId;
        item46.area = "3. 보안성 검토";
        item46.field = "비밀번호 기술적 보호조치";
        item46.subField = "임시 비밀번호";
        item46.no = "3.1.2.2";
        item46.item = "임시 비밀번호를 이용하여 정보주체가 로그인 할 경우, 비밀번호 변경을 강제하도록 안내하고 있습니까?";
        item46.riskFactors = "타인에게 공개되지 않은 정보의 의미는 타인이 비밀번호를 파악할 수 있도록 관리되어서는 안 된다는 것이다. 본인 이외의 비인가자나 공격자 등이 개인정보를 유출하는 등 불법행위를 할 수 있게 된다.";
        item46.improvementGuides = "(1) 비밀번호 사용 만료일 이전에 이용자에게 알려주어 변경 유도, 비밀번호 유효기간을 설정하여 강제 변경 등 안전한 비밀번호 설정 규칙을 적용한다. (2) 안전한 비밀번호 설정을 위해 한국인터넷진흥원(KISA)의 암호이용활성화 홈페이지 (https://seed.kisa.or.kr)에서 제공하는 \"패스워드 선택 및 이용 안내서\"나 비밀번호 안전성 검증 소프트웨어 등을 활용할 수 있다.";
        item46.law = "관련 법률 없음";
        items.add(item46);

        // 항목 47
        EvaluationItem item47 = new EvaluationItem();
        item47.id = 47;
        item47.companyId = companyId;
        item47.area = "3. 보안성 검토";
        item47.field = "비밀번호 기술적 보호조치";
        item47.subField = "하드코딩 방지";
        item47.no = "3.1.3.1";
        item47.item = "데이터베이스 연결을 위한 비밀번호를 소스코드 내부에 하드코딩하지 않도록 계획하고 있습니까?";
        item47.riskFactors = "프로그램 코드 내부에 하드코드된 비밀번호 또는 암호화키를 포함하여 내부 인증에 사용하거나 암호화를 수행하면 중요정보(관리자 정보, 암호화된 정보 등)가 유출될 수 있다.";
        item47.improvementGuides = "(1) 비밀번호는 암호화 하여 별도의 파일에 저장하여 사용한다. 또한 중요정보를 암호화하면, 상수가 아닌 암호화 키를 사용하도록 하며 소스코드 내부에 상수형태의 암호화 키를 저장해서 사용하지 않도록 한다.";
        item47.law = "관련 법률 없음";
        items.add(item47);

        // 항목 48
        EvaluationItem item48 = new EvaluationItem();
        item48.id = 48;
        item48.companyId = companyId;
        item48.area = "3. 보안성 검토";
        item48.field = "비밀번호 기술적 보호조치";
        item48.subField = "비밀번호 설정 규칙";
        item48.no = "3.1.4.1";
        item48.item = "영문, 숫자, 특수문자 두 종류 이상으로 조합한 최소 10자리 이상 또는 3종류 이상을 조합하여 최소 8자리 이상의 길이로 구성된 문자열로 비밀번호를 설정하도록 계획하고 있습니까?";
        item48.riskFactors = "회원가입 시 안전한 비밀번호 생성규칙이 적용되지 않아서 취약한 비밀번호로 회원가입이 가능할 경우 무차별 대입 공격으로 비밀번호가 누출될 수 있다.";
        item48.improvementGuides = "(1) 비밀번호 설정 시 한국인터넷진흥원 『비밀번호 선택 및 이용 안내서』 의 비밀번호 보안 지침을 적용 한다. (2) 비밀번호 재설정/변경 시 안전하게 변경할 수 있는 규칙을 정의해서 적용해야 한다.";
        item48.law = "관련 법률 없음";
        items.add(item48);

        // 항목 49
        EvaluationItem item49 = new EvaluationItem();
        item49.id = 49;
        item49.companyId = companyId;
        item49.area = "3. 보안성 검토";
        item49.field = "로그인 기술적 보호조치";
        item49.subField = "인증 수행 제한";
        item49.no = "3.2.1.1";
        item49.item = "반복적인 로그인 시도 횟수를 일정 횟수 이내로 통제하도록 계획하고 있습니까?";
        item49.riskFactors = "로그인 시도에 대한 횟수를 검사하지 않으면 로그인 시도 횟수와 상관없이 지속적으로 로그인 시도가 이루어지는 패스워드 무차별 대입 공격이 시도되어 계정정보가 노출될 수 있다.";
        item49.improvementGuides = "(1) 로그인 기능 구현 시, 인증시도 횟수를 제한하고 초과된 인증시도에 대해 인증제한 정책을 적용해야 한다. (2) 실패한 인증시도에 대한 정보를 로깅하여 인증시도 실패가 추적될 수 있게 해야 한다.";
        item49.law = "관련 법률 없음";
        items.add(item49);

        // 항목 50
        EvaluationItem item50 = new EvaluationItem();
        item50.id = 50;
        item50.companyId = companyId;
        item50.area = "3. 보안성 검토";
        item50.field = "로그인 기술적 보호조치";
        item50.subField = "무차별 대입 방지";
        item50.no = "3.2.2.1";
        item50.item = "로그인 실패 문구에 원인을 상세하게 표시하지 않도록 계획하고 있습니까?";
        item50.riskFactors = "로그인 시도 시 ID와 비밀번호 중 어떤 정보가 잘못된 것인지 알려주는 에러창은 해커에게 중요한 정보를 제공하므로 보안을 약하게 만든다.";
        item50.improvementGuides = "(1) 로그인 에러창을 띄울 때는 'ID 또는 비밀번호가 틀렸습니다' 등과 같이 아이디와 비밀번호 중 어떤 정보가 잘못된 것인지 알 수 없도록 메시지를 출력해야 한다.";
        item50.law = "관련 법률 없음";
        items.add(item50);

        // 항목 51
        EvaluationItem item51 = new EvaluationItem();
        item51.id = 51;
        item51.companyId = companyId;
        item51.area = "3. 보안성 검토";
        item51.field = "로그인 기술적 보호조치";
        item51.subField = "미사용 세션 관리";
        item51.no = "3.2.3.1";
        item51.item = "일정시간 동안 사용되지 않는 세션 정보는 강제적으로 삭제되도록 계획하고 있습니까?";
        item51.riskFactors = "인증 시 일정한 규칙이 존재하는 세션ID가 발급되거나 세션 타임아웃을 너무 길게 설정한 경우 공격자에 의해 사용자 권한이 도용될 수 있다.";
        item51.improvementGuides = "(1) 세션 타임아웃 시간은 중요기능의 경우 2~5분, 위험도가 낮은 경우에는 15~ 30분으로 설정하고, 이전 세션이 종료되지 않은 상태에서 새로운 세션이 생성되지 않도록 해야 한다. (2) 웹 브라우저 종료로 인한 세션종료는 서버 측에서 인지할 수 없으므로, 일정시간 동안 사용되지 않는 세션 정보는 강제적으로 삭제되도록 설계한다.";
        item51.law = "관련 법률 없음";
        items.add(item51);

        // 항목 52
        EvaluationItem item52 = new EvaluationItem();
        item52.id = 52;
        item52.companyId = companyId;
        item52.area = "3. 보안성 검토";
        item52.field = "암호화 기술적 보호조치";
        item52.subField = "저장 시 암호화";
        item52.no = "3.3.1.1";
        item52.item = "중요한 개인정보를 저장하는 경우, 안전한 방식으로 암호화 저장하도록 계획하고 있습니까?";
        item52.riskFactors = "메모리나 디스크에서 처리하는 중요데이터(개인정보, 인증정보, 금융정보)가 제대로 보호되지 않을 경우, 보안이나 데이터의 무결성이 훼손될 수 있다. 특히 프로그램이 개인정보, 인증정보 등의 사용자 중요정보 및 시스템 중요정보를 처리하는 과정에서 이를 평문으로 저장할 경우 공격자에게 민감한 정보가 노출될 수 있는 취약점이다.";
        item52.improvementGuides = "주민등록번호, 여권번호, 운전면허번호, 외국인등록번호, 신용카드번호, 계좌번호, 생체인식정보는 국내 및 미국, 일본, 유럽 등의 국외 암호 연구 관련 기관에서 사용 권고하는 안전한 암호알고리듬으로 암호화하여 저장하여야 한다.";
        item52.law = "관련 법률 없음";
        items.add(item52);

        // 항목 53
        EvaluationItem item53 = new EvaluationItem();
        item53.id = 53;
        item53.companyId = companyId;
        item53.area = "3. 보안성 검토";
        item53.field = "암호화 기술적 보호조치";
        item53.subField = "전송 시 암호화";
        item53.no = "3.3.2.1";
        item53.item = "고객의 비밀번호, 고유식별번호, 민감정보, 생체정보, 금융정보 등 중요정보를 인터넷 및 외부 시스템을 통해 전송 할 때는 SSL 방식 등의 암호화 통신을 하도록 계획하고 있습니까?";
        item53.riskFactors = "사용자 또는 시스템의 중요정보가 포함된 데이터를 평문으로 송·수신 또는 저장할 때 인가되지 않은 사용자 에게 민감한 정보가 노출될 수 있다.";
        item53.improvementGuides = "(1) 개인정보를 네트워크를 통해 전송할 때에는 불법적인 노출 또는 위·변조 방지를 위해, 전송구간에 암호화를 적용하여 개인정보가 안전하게 전송될 수 있도록 지원해야 한다.";
        item53.law = "관련 법률 없음";
        items.add(item53);

        // 항목 54
        EvaluationItem item54 = new EvaluationItem();
        item54.id = 54;
        item54.companyId = companyId;
        item54.area = "3. 보안성 검토";
        item54.field = "암호화 기술적 보호조치";
        item54.subField = "전송 시 암호화";
        item54.no = "3.3.2.2";
        item54.item = "중요상태정보나 인증, 권한결정에 사용되는 정보는 쿠키로 전송되지 않아야 하며, 불가피하게 전송해야 하는 경우에는 암호화해서 전송하도록 계획하고 있습니까?";
        item54.riskFactors = "서버는 사용자가 전달하는 쿠키, 환경변수, 파라미터 등을 충분히 검증하지 않고 사용할 경우 공격자는 이에 포함된 사용자의 권한, 역할 등을 나타내는 변수를 조작한 뒤 서버로 요청하여 상승된 권한으로 작업을 수행한다.";
        item54.improvementGuides = "(1) 쿠키, 환경변수, 파라미터 등 외부 입력값이 보안기능을 수행하는 함수의 인자로 사용되는 경우, 입력값에 대한 검증작업을 수행한 뒤 제한적으로 사용해야 한다. (2) 중요상태정보나 인증, 권한결정에 사용되는 정보는 쿠키로 전송되지 않아야 하며, 불가피하게 전송해야 하는 경우에는 해당 정보를 암호화해서 전송해야 한다.";
        item54.law = "관련 법률 없음";
        items.add(item54);

        // 항목 55
        EvaluationItem item55 = new EvaluationItem();
        item55.id = 55;
        item55.companyId = companyId;
        item55.area = "3. 보안성 검토";
        item55.field = "권한 설정 관리 체계";
        item55.subField = "접근 통제 조치";
        item55.no = "3.4.1.1";
        item55.item = "관리자 인터페이스 경로가 추측이 불가능하도록 설정하고, 계정/패스워드 인증 외에 접근 IP를 통한 접근 권한 설정(ACL)을 적용하도록 계획하고 있습니까?";
        item55.riskFactors = "인가되지 않은 개인정보취급자가 관리자 시스템의 중요 기능 또는 개인정보에 접근하여 발생하는 침해사고다.";
        item55.improvementGuides = "(1) 개인정보취급자별 계정을 각각 부여하고 계정 발급내역 및 상태 관리한다. (2) 개인정보처리시스템에 접속할 수 있는 계정은 개인정보취급자 별로 발급하고, 다른 개인정보 취급자와 공유되지 않도록 해야 한다. (3) 개인정보처리시스템에 대한 접근 권한을 업무 수행에 필요한 최소한의 범위로 업무 담당자에게 차등적으로 부여한다. (4) 인사이동, 퇴직 등으로 인해 개인정보취급자가 변경되었을 경우, 지체없이 개인정보처리 시스템의 접근 권한을 변경·말소한다.";
        item55.law = "관련 법률 없음";
        items.add(item55);

        // 항목 56
        EvaluationItem item56 = new EvaluationItem();
        item56.id = 56;
        item56.companyId = companyId;
        item56.area = "3. 보안성 검토";
        item56.field = "권한 설정 관리 체계";
        item56.subField = "접근 통제 조치";
        item56.no = "3.4.1.2";
        item56.item = "정보주체가 비밀번호 변경 등 중요 정보 접근 시 비밀번호 재확인 등 추가적인 인증이 적용되도록 계획하고 있습니까?";
        item56.riskFactors = "중요 정보 접근 시 추가 인증 절차 부재로 인한 개인정보 유출 및 변조 위험이다.";
        item56.improvementGuides = "(1) 중요정보 페이지 접근 시 OTP 등 재인증을 수행한다. (2) 인증 후 페이지에 아이디만을 인증 값으로 하여 변수로 관리되고 있는지 확인한다.";
        item56.law = "관련 법률 없음";
        items.add(item56);

        // 항목 57
        EvaluationItem item57 = new EvaluationItem();
        item57.id = 57;
        item57.companyId = companyId;
        item57.area = "3. 보안성 검토";
        item57.field = "어드민시스템 보안설정";
        item57.subField = "비정상적인 접근 보호";
        item57.no = "3.5.1.1";
        item57.item = "장기 미접속시 계정잠금을 통한 보호 대책이 적용되도록 계획하고 있습니까?";
        item57.riskFactors = "비활성화되어 방치된 계정은 탈취되더라도 인지하기 어렵고, 이를 통해 비인가자가 개인정보처리시스템에 접근하여 개인정보를 유출, 변조하거나 시스템을 오용하는 등 심각한 침해 사고로 이어질 수 있다.";
        item57.improvementGuides = "(1) 개인정보처리시스템의 계정 노출 등에 따라 다수의 PC에서 개인정보처리시스템 접근 시 정상적인 사용자가 이를 인지할 수 있도록 중복 로그인을 차단하도록 설계하는 것이 바람직하다. 중복로그인이 발생하는 경우 차단사실과 함께 중복로그인 위치의 간단한 정보도 함께 알려주는 것을 권장한다.";
        item57.law = "관련 법률 없음";
        items.add(item57);

        // 항목 58
        EvaluationItem item58 = new EvaluationItem();
        item58.id = 58;
        item58.companyId = companyId;
        item58.area = "3. 보안성 검토";
        item58.field = "어드민시스템 보안설정";
        item58.subField = "비정상적인 접근 보호";
        item58.no = "3.5.1.2";
        item58.item = "동시 접속 시 동시접속제한을 통한 보호 대책이 적용되도록 계획하고 있습니까?";
        item58.riskFactors = "사용자 계정 정보가 유출되더라도 동시 접속 제한이 없을 경우, 유출된 계정으로 여러 사용자가 동시에 시스템에 접근하여 인가된 사용자로 위장하거나, 개인정보를 대량으로 유출·변조하는 등 비정상적인 활동을 인지하기 어렵게 하여 심각한 피해를 발생시킬 수 있다.";
        item58.improvementGuides = "(1) 동일한 계정을 이용하여 동시접속을 수행하는 경우, 한 개의 접속만을 허용하도록 해야 한다. (2) 동시 접속이 허용되는 경우 신규 기기(IP, MAC 등)에서 동시 접속이 이루어지면 해당 사용자에게 알리는(SMS, 이메일 등) 기능을 설계할 수 있음";
        item58.law = "관련 법률 없음";
        items.add(item58);

        // 항목 59
        EvaluationItem item59 = new EvaluationItem();
        item59.id = 59;
        item59.companyId = companyId;
        item59.area = "3. 보안성 검토";
        item59.field = "어드민시스템 보안설정";
        item59.subField = "임시파일 및 캐시 통제";
        item59.no = "3.5.2.1";
        item59.item = "개인정보를 처리하는 기능 구현 시 더 이상 필요하지 않은 데이터에 대해 메모리를 초기화하여 중요데이터가 메모리에 남지 않도록 계획하고 있습니까?";
        item59.riskFactors = "개인정보를 처리한 후 더 이상 필요 없는 중요 데이터(비밀번호, 고유식별정보 등)가 메모리에 그대로 남아 있을 경우, 해당 메모리 영역이 다른 프로세스나 악의적인 공격자(메모리 덤프 공격, 스왑 파일 분석 등)에 의해 접근되어 개인정보가 유출될 수 있다.";
        item59.improvementGuides = "(1) 고유식별정보, 금융정보 등 민감한 개인정보를 표시하는 화면은 웹 브라우저의 뒤로 가기(Back) 버튼을 클릭해서 조회하면 기존 정보가 보이지 않도록 조치해야 한다. (No-Cache 설정 등) (2) 개인정보의 출력, 다운로드 등 처리과정에서 개인정보처리 단말기에 임시파일이 저장되지 않도록 조치해야 한다. (불가피하게 생성되는 경우에는 처리 완료 후 완전 삭제되도록 해야 함)";
        item59.law = "관련 법률 없음";
        items.add(item59);

        // 항목 60
        EvaluationItem item60 = new EvaluationItem();
        item60.id = 60;
        item60.companyId = companyId;
        item60.area = "3. 보안성 검토";
        item60.field = "대상시스템 보안설정";
        item60.subField = "입력값 검증";
        item60.no = "3.6.1.1";
        item60.item = "웹페이지 내 SQL 인젝션 취약점이 존재하지 못하도록 SQL 쿼리 입력에 대한 검증 로직을 구현하도록 계획하고 있습니까?";
        item60.riskFactors = "해당 취약점이 존재하는 경우 비정상적인 SQL 쿼리로 DBMS 및 데이터(Data)를 열람하거나 조작 가능하므로 사용자의 입력 값에 대한 필터링을 구현하여야 한다.";
        item60.improvementGuides = "1. 소스코드에 SQL 쿼리를 입력 값으로 받는 함수나 코드를 사용할 경우, 임의의 SQL 쿼리 입력에 대한 검증 로직을 구현하여 서버에 검증되지 않는 SQL 쿼리 요청 시 에러 페이지가 아닌 정상 페이지가 반환되도록 필터링 처리하고 웹 방화벽에 SQL 인젝션 관련 룰셋을 적용하여 SQL 인젝션 공격을 차단한다.";
        item60.law = "관련 법률 없음";
        items.add(item60);

        // 항목 61
        EvaluationItem item61 = new EvaluationItem();
        item61.id = 61;
        item61.companyId = companyId;
        item61.area = "3. 보안성 검토";
        item61.field = "대상시스템 보안설정";
        item61.subField = "입력값 검증";
        item61.no = "3.6.1.2";
        item61.item = "웹 사이트의 게시판, 자료실 등에 조작된 Server Side Script 파일 업로드 및 실행을 방지하도록 계획하고 있습니까?";
        item61.riskFactors = "해당 취약점이 존재할 경우 공격자는 조작된 Server Side Script 파일을 서버에 업로드 및 실행하여 시스템 관리자 권한 획득 또는 인접 서버에 대한 침입을 시도할 수 있음";
        item61.improvementGuides = "1. 업로드되는 파일에 대한 확장자 검증 및 실행 권한 제거";
        item61.law = "관련 법률 없음";
        items.add(item61);

        // 항목 62
        EvaluationItem item62 = new EvaluationItem();
        item62.id = 62;
        item62.companyId = companyId;
        item62.area = "3. 보안성 검토";
        item62.field = "대상시스템 보안설정";
        item62.subField = "입력값 검증";
        item62.no = "3.6.1.3";
        item62.item = "웹 사이트에서 파일 다운로드 시 허용된 경로 외 다른 경로의 파일 접근을 방지하도록 계획하고 있습니까?";
        item62.riskFactors = "1. 해당 취약점이 존재할 경우 공격자는 파일 다운로드 시 애플리케이션의 파라미터 값을 조작하여 웹 사이트의 중요한 파일(DB 커넥션 파일, 애플리케이션 파일 등) 또는 웹 서버 루트에 있는 중요한 설정 파일(passwd, shadow 등)을 다운받을 수 있음 2. cgi, jsp, php 등 파일 다운로드 기능을 제공해주는 애플리케이션에서 입력되는 경로를 검증하지 않는 경우 임의의 문자(../.. 등)나 주요 파일명의 입력을 통해 웹 서버의 홈 디렉터리를 벗어나서 임의의 위치에 있는 파일을 열람하거나 다운받는 것이 가능함";
        item62.improvementGuides = "1. 다운로드 시 허용된 경로 이외의 디렉터리와 파일에 접근할 수 없도록 구현";
        item62.law = "관련 법률 없음";
        items.add(item62);

        // 항목 63
        EvaluationItem item63 = new EvaluationItem();
        item63.id = 63;
        item63.companyId = companyId;
        item63.area = "3. 보안성 검토";
        item63.field = "대상시스템 보안설정";
        item63.subField = "입력값 검증";
        item63.no = "3.6.1.4";
        item63.item = "웹 사이트 내 크로스사이트 스크립팅 취약점을 제거하도록 계획하고 있습니까?";
        item63.riskFactors = "웹 애플리케이션에서 사용자 입력 값에 대한 필터링이 제대로 이루어지지 않을 경우, 공격자는 사용자 입력 값을 받는 게시판, URL 등에 악의적인 스크립트(Javascript, VBScript, ActiveX, Flash 등)를 삽입하여 게시글이나 이메일을 읽는 사용자의 쿠키(세션)를 탈취하여 도용하거나 악성코드 유포 사이트로 Redirect 할 수 있음";
        item63.improvementGuides = "1. 웹 사이트의 게시판, 1:1 문의, URL 등에서 사용자 입력 값에 대해 검증 로직을 추가하거나 입력되더라도 실행되지 않게 함 2. 부득이하게 웹페이지에서 HTML을 사용하는 경우 HTML 코드 중 필요한 코드에 대해서만 입력되게 설정";
        item63.law = "관련 법률 없음";
        items.add(item63);

        // 항목 64
        EvaluationItem item64 = new EvaluationItem();
        item64.id = 64;
        item64.companyId = companyId;
        item64.area = "3. 보안성 검토";
        item64.field = "대상시스템 보안설정";
        item64.subField = "입력값 검증";
        item64.no = "3.6.1.5";
        item64.item = "웹 사이트 내 운영체제 명령 실행 취약점을 방지하도록 계획하고 있습니까?";
        item64.riskFactors = "해당 취약점이 존재하는 경우 부적절하게 권한이 변경되거나 시스템 동작 및 운영에 악영향을 줄 가능성이 있으므로 \"|\", \"&\", \";\", \"`\" 문자에 대한 필터링 구현이 필요함";
        item64.improvementGuides = "1. 취약한 버전의 웹 서버 및 웹 애플리케이션 서버는 최신 버전으로 업데이트를 적용해야 함 2. 애플리케이션은 운영체제로부터 명령어를 직접적으로 호출하지 않도록 구현하는 게 좋지만, 부득이하게 사용해야 할 경우 소스 코드나 웹방화벽에서 특수문자, 특수 구문에 대한 검증을 할 수 있도록 조치해야 함";
        item64.law = "관련 법률 없음";
        items.add(item64);

        // 항목 65
        EvaluationItem item65 = new EvaluationItem();
        item65.id = 65;
        item65.companyId = companyId;
        item65.area = "3. 보안성 검토";
        item65.field = "대상시스템 보안설정";
        item65.subField = "입력값 검증";
        item65.no = "3.6.2.1";
        item65.item = "예측 가능한 폴더의 위치 사용 여부 및 불필요한 파일을 방지하도록 계획하고 있습니까?";
        item65.riskFactors = "폴더나 파일명의 위치가 예측 가능하여 쉽게 노출될 경우 공격자는 이를 악용하여 대상에 대한 정보를 획득하고 민감한 데이터에 접근 가능";
        item65.improvementGuides = "1. 웹 루트 디렉터리 이하 모든 불필요한 파일 및 샘플 페이지 삭제";
        item65.law = "관련 법률 없음";
        items.add(item65);

        // 항목 66
        EvaluationItem item66 = new EvaluationItem();
        item66.id = 66;
        item66.companyId = companyId;
        item66.area = "3. 보안성 검토";
        item66.field = "대상시스템 보안설정";
        item66.subField = "입력값 검증";
        item66.no = "3.6.2.2";
        item66.item = "웹 서버 내 디렉터리 인덱싱 취약점을 제거하도록 계획하고 있습니까?";
        item66.riskFactors = "해당 취약점이 존재할 경우 브라우저를 통해 특정 디렉터리 내 파일 리스트를 노출하여 응용시스템의 구조를 외부에 허용할 수 있고, 민감한 정보가 포함된 설정 파일 등이 노출될 경우 보안상 심각한 위험을 초래할 수 있음";
        item66.improvementGuides = "1. 웹 서버 설정을 변경하여 디렉터리 파일 리스트가 노출되지 않도록 설정";
        item66.law = "관련 법률 없음";
        items.add(item66);

        // 항목 67
        EvaluationItem item67 = new EvaluationItem();
        item67.id = 67;
        item67.companyId = companyId;
        item67.area = "3. 보안성 검토";
        item67.field = "대상시스템 보안설정";
        item67.subField = "입력값 검증";
        item67.no = "3.6.2.3";
        item67.item = "인증이 필요한 웹 사이트의 중요(관리자 페이지, 회원변경 페이지 등) 페이지에 대해 접근제어 설정을 하도록 계획하고 있습니까?";
        item67.riskFactors = "인증이 필요한 웹 사이트의 중요(관리자 페이지, 회원변경 페이지 등) 페이지에 대한 접근 제어가 미흡할 경우 하위 URL 직접 접근, 스크립트 조작 등의 방법으로 중요한 페이지에 대한 접근이 가능함";
        item67.improvementGuides = "1. 인증이 필요한 페이지의 경우 페이지별 권한 체크 로직 구현s";
        item67.law = "관련 법률 없음";
        items.add(item67);


        return items;
    }
}