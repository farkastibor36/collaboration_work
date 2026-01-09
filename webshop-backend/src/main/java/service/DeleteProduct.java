package service;

import exceptions.FailedToDeleteException;
import exceptions.MissingParamException;
import lombok.RequiredArgsConstructor;

import java.util.Scanner;
@RequiredArgsConstructor
public class DeleteProduct {
    private final ProductCRUDService productCRUDService;
    private Scanner scanner = new Scanner(System.in);

    public void removeProduct() throws FailedToDeleteException {
        System.out.println("Enter product id to delete:");
        int userInput = Integer.parseInt(scanner.nextLine());
        if (userInput <= 0) {
            throw new MissingParamException("Product id cannot be null");
        }
        productCRUDService.deleteProduct(userInput);
        System.out.println("Product deleted successfully.");
    }
}