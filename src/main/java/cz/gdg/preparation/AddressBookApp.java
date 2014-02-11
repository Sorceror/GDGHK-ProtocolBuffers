package cz.gdg.preparation;

import cz.gdg.preparation.AddressBookProto.AddressBook;
import cz.gdg.preparation.AddressBookProto.Person;

import java.io.*;

/**
 * Demo for google protocol buffers presentation
 * @author Pavel Janecka
 */
public class AddressBookApp {
    private static final String DATA_FILE = "addressBook.dat";
    private AddressBook.Builder addressBook;

    public AddressBookApp() throws IOException {
        addressBook = AddressBook.newBuilder();

        loadAddressBook();

        listPersons();
        readAllPersons();
        listPersons();

        saveAddressBook();
    }

    /**
     * Read persons to address book as defined by user
     * @throws IOException when user input fails
     */
    private void readAllPersons() throws IOException {
        boolean addAnother = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (addAnother) {
            System.out.println("Add another item to address book?");
            String line = reader.readLine();
            if (line.equalsIgnoreCase("yes")) {
                addressBook.addPerson(readPerson(reader));
            } else {
                addAnother = false;
            }
        }
    }

    /**
     * Read one person from user and return it's instance
     * @param reader {@link BufferedReader} user's input
     * @return {@link Person} instance
     * @throws IOException when user input fails
     */
    private Person readPerson(BufferedReader reader) throws IOException {
        Person.Builder person = Person.newBuilder();

        System.out.print("Enter ID: ");
        person.setId(Integer.valueOf(reader.readLine()));

        System.out.print("Enter name: ");
        person.setName(reader.readLine());

        System.out.print("Enter email (blank for none): ");
        String email = reader.readLine();
        if (email.length() > 0)
            person.setEmail(email);

        while (true) {
            System.out.print("Enter phone number (blank for finish entering)");
            String number = reader.readLine();
            if (number.length() > 0) {
                Person.PhoneNumber.Builder phoneNumber = Person.PhoneNumber.newBuilder().setNumber(number);
                System.out.print("Is it (m)obile, (h)ome or (w)ork number?");
                String type = reader.readLine();
                if (type.equalsIgnoreCase("m"))
                    phoneNumber.setType(Person.PhoneType.MOBILE);
                else if (type.equalsIgnoreCase("h"))
                    phoneNumber.setType(Person.PhoneType.HOME);
                else phoneNumber.setType(Person.PhoneType.WORK);

                person.addPhone(phoneNumber);
            } else {
                break;
            }
        }

        return person.build();
    }

    /**
     * Load address book from file
     */
    private void loadAddressBook() {
        try {
            addressBook = addressBook.mergeFrom(new FileInputStream(DATA_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * List all persons in address book
     */
    private void listPersons() {
        System.out.println("------ ADDRESS BOOK ------");
        for (Person person : addressBook.getPersonList()) {
            System.out.println("** PERSON **");
            System.out.println("Person ID: \t" + person.getId());
            System.out.println("Name: \t\t" + person.getName());
            if (person.hasEmail()) {
                System.out.println("Email: \t\t" + person.getEmail());
            }

            for (Person.PhoneNumber phoneNumber : person.getPhoneList()) {
                switch (phoneNumber.getType()) {
                    case MOBILE:
                        System.out.print("Mobile: \t");
                        break;
                    case HOME:
                        System.out.print("Home \t");
                        break;
                    case WORK:
                        System.out.print("Work \t");
                        break;
                }
                System.out.println(phoneNumber.getNumber());
            }
        }
    }

    /**
     * Save address book to file
     */
    private void saveAddressBook() {
        try {
            addressBook.build().writeTo(new FileOutputStream(DATA_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            new AddressBookApp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
