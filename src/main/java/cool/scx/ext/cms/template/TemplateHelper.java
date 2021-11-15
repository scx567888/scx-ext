package cool.scx.ext.cms.template;

import cool.scx.ScxContext;
import cool.scx.annotation.FromUpload;
import cool.scx.bo.UploadedEntity;
import cool.scx.util.FileUtils;
import cool.scx.vo.Json;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Cms 模板
 *
 * @author scx567888
 * @version 0.3.6
 */
public final class TemplateHelper {

    /**
     * 获取文件夹下的文件列表
     *
     * @param filePath 文件路径
     * @return 文件列表
     * @throws IOException if any.
     */
    private static List<TemplateInfo> getTemplateList(String filePath) throws IOException {
        var fileList = new LinkedList<TemplateInfo>();
        var path = Paths.get(filePath);
        Files.walkFileTree(path, new FileVisitor<>() {
            //访问文件夹之前自动调用此方法
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                var fileInfo = new TemplateInfo();
                fileInfo.type = "Directory";
                getFileVisitResult(dir, fileInfo, path, fileList);
                return FileVisitResult.CONTINUE;
            }

            //访问文件时自动调用此方法
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                var fileInfo = new TemplateInfo();
                fileInfo.type = "File";
                getFileVisitResult(file, fileInfo, path, fileList);
                return FileVisitResult.CONTINUE;
            }

            //访问文件失败时自动调用此方法
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }

            //访问文件夹之后自动调用此方法
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });
        return fileList;
    }

    private static void getFileVisitResult(Path file, TemplateInfo templateInfo, Path path, LinkedList<TemplateInfo> fileList) {
        templateInfo.id = file.getFileName().toString();
        templateInfo.parentID = file.getParent().toFile().getPath();
        if (path.toString().equals(templateInfo.parentID)) {
            templateInfo.parentID = "0";
        } else {
            templateInfo.parentID = file.getParent().getFileName().toString();
        }
        templateInfo.filePath = file.toFile().getPath();
        fileList.add(templateInfo);
    }

    /**
     * 判断文件是否为 cms 目录下的文件
     *
     * @param path 路径
     * @return 结构
     */
    private static boolean checkPath(String path) {
        return path.startsWith(ScxContext.easyConfig().templateRoot().getPath());
    }

    /**
     * <p>Index.</p>
     *
     * @return a {@link cool.scx.vo.Json} object.
     * @throws java.io.IOException if any.
     */
    public static Json index() throws IOException {
        var allTemplateList = getTemplateList(ScxContext.easyConfig().templateRoot().getPath());
        // 让文件夹永远在前边
        var directoryList = allTemplateList.stream().filter(templateInfo -> "Directory".equals(templateInfo.type)).collect(Collectors.toList());
        var fileList = allTemplateList.stream().filter(templateInfo -> "File".equals(templateInfo.type)).collect(Collectors.toList());
        directoryList.addAll(fileList);
        return Json.ok().put("cmsRootTreeList", directoryList);
    }

    /**
     * 获取文件内容
     *
     * @param filePath 文件路径
     * @return a {@link cool.scx.vo.Json} object.
     */
    public static Json getFileContent(String filePath) {
        try {
            boolean b = checkPath(filePath);
            if (b) {
                String fileContent = Files.readString(Paths.get(filePath));
                return Json.ok().put("fileContent", fileContent);
            } else {
                return Json.ok().put("fileContent", "文件无法访问");
            }
        } catch (Exception exception) {
            return Json.ok().put("fileContent", "此文件无法编辑");
        }
    }

    /**
     * <p>setFileContent.</p>
     *
     * @param filePath    文件路径
     * @param fileContent 文件内容
     * @return a {@link cool.scx.vo.Json} object.
     */
    public static Json setFileContent(String filePath, String fileContent) {
        boolean b = checkPath(filePath);
        if (b) {
            FileUtils.setFileContent(filePath, fileContent);
            return getFileContent(filePath);
        } else {
            return Json.fail("文件无法访问");
        }
    }

    /**
     * <p>delete.</p>
     *
     * @param filePath a {@link java.util.Map} object.
     * @return a {@link cool.scx.vo.Json} object.
     * @throws java.io.IOException if any.
     */
    public static Json delete(String filePath) throws IOException {
        boolean b = checkPath(filePath);
        if (b) {
            var file = Paths.get(filePath);
            FileUtils.deleteIfExists(file);
            return Json.ok();
        } else {
            return Json.fail("文件无法访问");
        }
    }

    /**
     * <p>upload.</p>
     *
     * @param file     a {@link java.io.File} object.
     * @param filePath a {@link java.lang.String} object.
     * @return a {@link cool.scx.vo.Json} object.
     */
    public static Json upload(@FromUpload UploadedEntity file, String filePath) {
        if (checkPath(filePath)) {
            FileUtils.fileAppend(Path.of(filePath, file.fileName()), file.buffer().getBytes());
            return Json.ok();
        } else {
            return Json.fail("文件无法访问");
        }
    }

    /**
     * <p>rename.</p>
     *
     * @param newFilePath 原文件路径
     * @param oldFilePath 新文件路径
     * @return a {@link cool.scx.vo.Json} object.
     */
    public static Json rename(String newFilePath, String oldFilePath) {
        var b = checkPath(newFilePath);
        var b1 = checkPath(oldFilePath);
        if (b && b1) {
            Path path = Paths.get(oldFilePath);
            String parent = path.getParent().toFile().getPath();
            boolean b2 = path.toFile().renameTo(new File(parent + "\\" + newFilePath));
            return b2 ? Json.ok() : Json.fail();
        } else {
            return Json.fail("文件无法访问");
        }
    }


}
