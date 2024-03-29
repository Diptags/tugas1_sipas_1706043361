package com.sipas.app.controller;

import com.sipas.app.model.AsuransiModel;
import com.sipas.app.model.DiagnosisPenyakitModel;
import com.sipas.app.model.EmergencyContactModel;
import com.sipas.app.model.PasienModel;
import com.sipas.app.other.AddPasienHandler;
import com.sipas.app.other.ChangePasienHandler;
import com.sipas.app.other.HandlingAsuransiDiagnosisSearch;
import com.sipas.app.service.AsuransiService;
import com.sipas.app.service.DiagnosisPenyakitService;
import com.sipas.app.service.EmergencyContactService;
import com.sipas.app.service.PasienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PasienController {

    @Autowired
    private PasienService pasienService;

    @Autowired
    private AsuransiService asuransiService;

    @Autowired
    private EmergencyContactService emergencyContactService;

    @Autowired
    private DiagnosisPenyakitService diagnosisPenyakitService;

    // Membuka halaman utama SIPAS
    @GetMapping("/")
    public String showHomePage(Model model) {
        List<PasienModel> pasienList = pasienService.getPasienList();
        model.addAttribute("pasienList", pasienList);
        return "homepage";
    }

    // Membuka form untuk menambahkan pasien
    @GetMapping(value = "/pasien/tambah")
    public String showAddPasienForm(@ModelAttribute("addHandler") AddPasienHandler addHandler, Model model) {
        List<AsuransiModel> asuransiList = asuransiService.getAsuransiList();
        model.addAttribute("asuransiList", asuransiList);
        return "pasien-add";
    }

    // Melakukan submit form untuk menambahkan pasien
    @PostMapping(value = "/pasien/tambah")
    public String submitAddPasienForm(@ModelAttribute("addHandler") AddPasienHandler addHandler, Model model) {
        String kodePasien = pasienService.addPasien(addHandler);
        model.addAttribute(
                "pesan", "Pasien " + addHandler.getNamaPasien() + " dengan kode pasien " + kodePasien + " berhasil ditambahkan!"
        );
        return "message-info";
    }

    // Membuka detail dari pasien berdasarkan NIK pasien
    @GetMapping(value = "/pasien")
    public String showPasienInfoByNik(@RequestParam(value = "nikPasien") String nikPasien, Model model) {
        PasienModel pasien = pasienService.getPasienByNikPasien(nikPasien);
        model.addAttribute("pasien", pasien);
        return "pasien-detail";
    }

    // Membuka form untuk mengubah pasien
    @GetMapping(value = "/pasien/ubah/{nikPasien}")
    public String showChangePasienForm(@PathVariable String nikPasien,
                                       @ModelAttribute("changeHandler") ChangePasienHandler changeHandler,
                                       Model model) {
        PasienModel pasien = pasienService.getPasienByNikPasien(nikPasien);
        model.addAttribute("pasien", pasien);
        return "pasien-change";
    }

    // Submit form untuk mengubah pasien
    @PostMapping(value = "/pasien/ubah/{nikPasien}")
    public String showChangePasienForm(@PathVariable String nikPasien,
                                       @ModelAttribute PasienModel pasien,
                                       @ModelAttribute EmergencyContactModel emergencyContact,
                                       @ModelAttribute("changeHandler") ChangePasienHandler changeHandler,
                                       Model model) {
        String kodePasien = pasienService.changePasienData(pasien, changeHandler);
        model.addAttribute(
                "pesan", "Pasien " + changeHandler.getNamaPasien() + " berhasil diubah, Kode pasien: " + kodePasien
        );
        return "message-info";
    }

    // Membuka form untuk menambah diagnosis penyakit pasien
    @GetMapping(value = "/pasien/{nikPasien}/tambah-diagnosis")
    public String showAddDiagnosisPenyakitForm(@PathVariable String nikPasien,
                                               @ModelAttribute("selectedDiagnosisPenyakit") DiagnosisPenyakitModel selectedDiagnosisPenyakit,
                                               Model model) {
        PasienModel pasien = pasienService.getPasienByNikPasien(nikPasien);
        List<DiagnosisPenyakitModel> diagnosisPenyakitList = diagnosisPenyakitService.getDiagnosisPenyakitList();

        model.addAttribute("pasien", pasien);
        model.addAttribute("diagnosisPenyakitList", diagnosisPenyakitList);
        return "pasien-add-diagnosis";
    }

    // Melakukan submit form penambahan diagnosis penyakit pasien
    @PostMapping(value = "/pasien/{nikPasien}/tambah-diagnosis")
    public String submitAddDiagnosisPenyakitForm(@PathVariable String nikPasien,
                                                 @ModelAttribute("selectedDiagnosisPenyakit") DiagnosisPenyakitModel selectedDiagnosisPenyakit,
                                                 Model model) {
        String[] temporaryData = selectedDiagnosisPenyakit.getKode().split(" ");
        Long idDiagnosisPenyakit = (Long) Long.parseLong(temporaryData[0]);
        Long idPasien = (Long) Long.parseLong(temporaryData[1]);

        DiagnosisPenyakitModel penyakit = diagnosisPenyakitService.getDiagnosisPenyakitByIdDiagnosisPenyakit(idDiagnosisPenyakit);
        PasienModel pasien = pasienService.getPasienByIdPasien(idPasien);

        pasienService.addDiagnosisToPasien(pasien, penyakit);
        model.addAttribute("pesan", "Data diagnosis penyakit berhasil ditambahkan kepada pasien!");
        return "message-info";
    }

    // Melakukan penghapusan data pasien
    @GetMapping(value = "/pasien/hapus/{idPasien}")
    public String deleteDiagnosisPenyakit(@PathVariable("idPasien") Long idPasien, Model model)
    {
        PasienModel pasien = pasienService.getPasienByIdPasien(idPasien);
        pasienService.deletePasien(pasien);
        model.addAttribute("pesan", "Data pasien berhasil dihapus!");
        return "message-info";
    }

    // Membuka halaman pencarian pasien berdasarkan Asuransi diagnosis penyakit
    @GetMapping(value = "/pasien/cari/diagnosis")
    public String showPasienInfoByDiagnosisForm(@ModelAttribute("selectedDiagnosisPenyakit") DiagnosisPenyakitModel selectedDiagnosisPenyakit,
                                                Model model) {
        List<DiagnosisPenyakitModel> diagnosisPenyakitList = diagnosisPenyakitService.getDiagnosisPenyakitList();
        model.addAttribute("diagnosisPenyakitList", diagnosisPenyakitList);
        return "pasien-find-gender-diagnosis";
    }

    // Submit form pencarian pasien berdasarkan diagnosis & menampilkan berdasarkan gender
    @PostMapping(value = "/pasien/cari/diagnosis")
    public String submitPasienInfoByDiagnosisForm(@ModelAttribute("selectedDiagnosisPenyakit") DiagnosisPenyakitModel selectedDiagnosisPenyakit,
                                                  Model model) {
        Long idDiagnosisPenyakit = selectedDiagnosisPenyakit.getIdDiagnosisPenyakit();
        DiagnosisPenyakitModel diagnosisPenyakit = diagnosisPenyakitService.getDiagnosisPenyakitByIdDiagnosisPenyakit(idDiagnosisPenyakit);
        List<PasienModel> listPenderita = diagnosisPenyakit.getListPenderita();

        int counterMan = 0;
        int counterWoman = 0;

        for (PasienModel pasien : listPenderita) {
            if (pasien.getJenisKelamin().equals(0)) {
                counterMan++;
            } else {
                counterWoman++;
            }
        }
        model.addAttribute("namaPenyakit", diagnosisPenyakit.getNama());
        model.addAttribute("counterMan", counterMan);
        model.addAttribute("counterWoman", counterWoman);
        return "pasien-find-gender-diagnosis-result";
    }

    // Membuka halaman pencarian pasien berdasarkan Asuransi dan/atau diagnosis penyakit
    @GetMapping(value = "/pasien/cari/asuransi-diagnosis")
    public String showPasienInfoByAsuransiDiagnosisForm(@ModelAttribute("searchHandler") HandlingAsuransiDiagnosisSearch searchHandler,
                                                        Model model) {
        List<DiagnosisPenyakitModel> diagnosisPenyakitList = diagnosisPenyakitService.getDiagnosisPenyakitList();
        List<AsuransiModel> asuransiList = asuransiService.getAsuransiList();

        model.addAttribute("diagnosisPenyakitList", diagnosisPenyakitList);
        model.addAttribute("asuransiList", asuransiList);
        model.addAttribute("searchHandler", new HandlingAsuransiDiagnosisSearch());
        return "pasien-find-asuransi-diagnosis";
    }

    // Submit form pencarian pasien berdasarkan Asuransi dan/atau diagnosis penyakit
    @PostMapping(value = "/pasien/cari/asuransi-diagnosis")
    public String submitPasienInfoByAsuransiDiagnosisForm(@ModelAttribute("searchHandler") HandlingAsuransiDiagnosisSearch searchHandler,
                                                          Model model) {
        Long idDiagnosisPenyakit = searchHandler.getIdDiagnosisPenyakit();
        Long idAsuransi = searchHandler.getIdAsuransi();

        DiagnosisPenyakitModel diagnosisPenyakit = diagnosisPenyakitService.getDiagnosisPenyakitByIdDiagnosisPenyakit(idDiagnosisPenyakit);
        AsuransiModel asuransi = asuransiService.getAsuransiByIdAsuransi(idAsuransi);

        if (idDiagnosisPenyakit.equals((long) 0) || idAsuransi.equals((long) 0)) {
            if (idDiagnosisPenyakit.equals((long) 0) && idAsuransi.equals((long) 0)) {
                model.addAttribute("searchSuccess", 0);
            } else if (idDiagnosisPenyakit.equals((long) 0)) {
                List<PasienModel> listPemilik = asuransi.getListPemilik();
                model.addAttribute("dataPencarian", listPemilik);
                model.addAttribute("searchSuccess", 1);
            } else {
                List<PasienModel> listPenderita = diagnosisPenyakit.getListPenderita();
                model.addAttribute("dataPencarian", listPenderita);
                model.addAttribute("searchSuccess", 1);
            }
        } else {
            List<PasienModel> listPemilik = asuransi.getListPemilik();
            List<PasienModel> listPenderita = diagnosisPenyakit.getListPenderita();
            listPemilik.retainAll(listPenderita);

            if (listPemilik.size() == 0) {
                model.addAttribute("searchSuccess", 0);
            } else {
                System.out.println(listPemilik);
                model.addAttribute("dataPencarian", listPemilik);
                model.addAttribute("searchSuccess", 1);
            }
        }
        return "pasien-find-asuransi-diagnosis-result";
    }
}
