//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.nio.file.FileVisitOption;
//import java.nio.file.FileVisitResult;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.SimpleFileVisitor;
//import java.nio.file.attribute.BasicFileAttributes;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.EnumSet;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//import org.apache.commons.lang3.StringUtils;
//import org.junit.jupiter.api.Test;
//
//import model.analyze.Loader;
//import model.analyze.Writer;
//import model.analyze.beans.MemoryFile;
//
//class Convert {
//
//	private File file = new File("D://toConvert");
//	private File fileConvert = new File("D://toConvert//Convert");
//	private Map<String, String> toConvert = new HashMap<String, String>();
//	private List<String> toRemove = new ArrayList<>();
//	private List<String> deleteDoublon = new ArrayList<>();
//
//	private void alimenteTable() {
//		toConvert.put("[MANUAL]", "[REGISTRO]");
//		toConvert.put("[MANUAL_TITULO]", "[TITULO]");
//		toConvert.put("[EDITORIAL]", "[FUENTE/EDITORIAL]");
//		toConvert.put("[PROGRAMA_OFICIAL]", "[PROGRAMA_CURRICULAR]");
//		toConvert.put("[UNIDAD]", "[UNIDAD_DIDACTICA]");
//		toConvert.put("[UNIDAD_TITULO]", "[UNIDAD_DIDACTICA_TITULO]");
//		toConvert.put("[TEXTO_NIVEL_MCER]", "[TEXTO_NIVEL_COMPETENCIAL]");
//		toConvert.put("[ACTIVIDAD_VOCABULARIO_ENUNCIADO]", "[ACTIVIDAD_ENUNCIADO]");
//		toRemove.add("[VOCABULARIO_TEXTO_CLASE]");
//		toRemove.add("[VOCABULARIO_EXTRA_CLASE]");
//		toRemove.add("[ACTIVIDAD_TIPO]");
//		deleteDoublon.add("[TEXTO_TITULO]");
//
//	}
//
//	private List<MemoryFile> parcoursListeFichier() throws IOException {
//		List<MemoryFile> listeSortie = new ArrayList<>();
//		Files.walkFileTree(Paths.get(file.toURI()), EnumSet.noneOf(FileVisitOption.class), 1,
//				new SimpleFileVisitor<Path>() {
//					@Override
//					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//						if (!Files.isDirectory(file)) {
//							System.out.println(String.format("Process file %s", file.toString()));
//							listeSortie.add(new Loader(file).getMemoryFile());
//						}
//						return FileVisitResult.CONTINUE;
//					}
//				});
//		return listeSortie;
//	}
//
//	private List<String> processFile(MemoryFile memoryFile) {
//		List<String> lines = new LinkedList<String>();
//		String previousLine = StringUtils.EMPTY;
//		memoryFile.createIterator();
//		while (memoryFile.hasLine()) {
//			String l = memoryFile.getNextLine();
//			if (existInLine(deleteDoublon, l) && existInLine(deleteDoublon, previousLine)) {
//				previousLine = l;
//				continue;
//			}
//			if (existInLine(toRemove, l)) {
//				previousLine = l;
//				continue;
//			}
//			Optional<String> ifExistInLine = getIfExistInLine(toConvert.keySet(), l);
//			if (ifExistInLine.isPresent()) {
//				l = StringUtils.replace(l, ifExistInLine.get(), toConvert.get(ifExistInLine.get()));
//			}
//			lines.add(l);
//			previousLine = l;
//		}
//		return lines;
//	}
//
//	private Boolean existInLine(Collection<String> list, String line) {
//		return list.stream().anyMatch(s -> StringUtils.contains(line, s));
//	}
//
//	private Optional<String> getIfExistInLine(Collection<String> list, String line) {
//		return list.stream().filter(s -> StringUtils.contains(line, s)).findFirst();
//	}
//
//	private void writeFile(List<String> lines, String nameFile) {
//		try (Writer writer = new Writer(fileConvert, nameFile)) {
//			lines.forEach(l -> {
//				try {
//					writer.writeLineWithBreakLineAfter(l);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			});
//			
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	void test() {
//		alimenteTable();
//		try {
//			List<MemoryFile> parcoursListeFichier = parcoursListeFichier();
//
//			parcoursListeFichier.stream().forEach(mf -> {
//				List<String> processFile = processFile(mf);
//				writeFile(processFile, mf.nameFile());
//				System.out.println("File converted");
//			});
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//}
