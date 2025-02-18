package com.oracle.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.oracle.Vos.ReturnUiVo;
import com.oracle.helper.CSVHelper;
import com.oracle.model.Return;
import com.oracle.model.ReturnItems;
import com.oracle.repository.ReturnItemRepo;
import com.oracle.repository.ReturnRepository;
import com.oracle.service.ReturnCsvService;

import springfox.documentation.service.ResponseMessage;

@CrossOrigin
@RestController
@RequestMapping("/app")
public class ReturnController {
	@Autowired
	ReturnCsvService returnCsvservice;

	@Autowired
	ReturnItemRepo returnItemRepo;

	@Autowired
	ReturnRepository returnRepo;

	@PostMapping("/file-upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
		String message = "";

		if (CSVHelper.hasCSVFormat(file)) {
			try {
				ReturnUiVo returns = returnCsvservice.save(file);

				return ResponseEntity.ok(returns);
			} catch (Exception e) {
				message = "Could not- upload the file: " + file.getOriginalFilename() + "!";
				return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
						.body(new com.oracle.message.ResponseMessage(message, ""));
			}
		}

		message = "Please upload a csv file!";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new com.oracle.message.ResponseMessage(message, ""));
	}

	@GetMapping(path = "/validate/{Id}")
	public ResponseEntity<?> getReturnItemById(@PathVariable String Id) {
		List<Return> updatedReturn = new ArrayList<Return>();
		List<ReturnItems> updatedListItem = new ArrayList<ReturnItems>();
		System.out.println("----");
		ReturnUiVo uiVo = new ReturnUiVo();
		List<ReturnItems> listItem = returnItemRepo.findReturnItemByReturnId(Id);
		Return ret = returnRepo.findById(Id).get();
		for (ReturnItems rItem : listItem) {
			if (rItem.getMemberDob() == null) {
				// rItem.setComment("Date of Birth is empty");
				updatedListItem.add(returnItemRepo.save(rItem));
				break;
			} else if (rItem.getMemFirstName() == null) {
				// rItem.setComment("First name is empty");
				updatedListItem.add(returnItemRepo.save(rItem));
				break;
			} else if (rItem.getMemeLastName() == null) {
				// rItem.setComment("First name is empty");
				updatedListItem.add(returnItemRepo.save(rItem));
				break;
			} else if (rItem.getMemberNrc() == null) {
				// rItem.setComment("First name is empty");
				// returnItemRepo.save(rItem);
				updatedListItem.add(returnItemRepo.save(rItem));
				break;
			} 
			
			else {
				rItem.setStatus("Validated");
				ret.setStatus("Validated");
				updatedReturn.add(returnRepo.save(ret));
				updatedListItem.add(returnItemRepo.save(rItem));
				// returnItemRepo.save(rItem);
			}
		}
		uiVo.setReturns(updatedReturn);
		uiVo.setItems(updatedListItem);
		return ResponseEntity.ok(uiVo);

	}

	@GetMapping(path = "/getItemsByReturnId/{returnId}")
	public List<ReturnItems> getReturnItemsByReturnId(@PathVariable String returnId) {
		// Return r=new Return();
		// r.setId(returnId);
		return returnItemRepo.findReturnItemByReturnId(returnId);
		// return returnItemRepo.findByRetur(r);

	}
}
