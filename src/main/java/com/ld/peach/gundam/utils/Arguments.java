package com.ld.peach.gundam.utils;

import lombok.Getter;
import org.apache.commons.cli.*;

import static com.ld.peach.gundam.utils.Constant.*;

/**
 * 这个类负责对从命令行中获取的参数进行解析
 * 参数有如下四种 使用的时候使用简写就可以了
 * <p>
 * -p
 * -g 是否展示图像
 * -t
 * -b
 *
 * @author LD
 */
public class Arguments {

    @Getter
    private final int batchSize;
    @Getter
    private final boolean graphics;
    @Getter
    private final boolean preTrained;
    @Getter
    private final boolean testing;


    public Arguments(CommandLine cmd) {
        graphics = cmd.hasOption(GRAPHICS_PARAM);

        if (cmd.hasOption(BATCH_SIZE_PARAM)) {
            batchSize = Integer.parseInt(cmd.getOptionValue(BATCH_SIZE_PARAM));
        } else {
            batchSize = 32;
        }

        preTrained = cmd.hasOption(PRE_TRAINED_PARAM);

        testing = cmd.hasOption(TESTING_PARAM);
    }

    public static Arguments parseArgs(String[] args) throws ParseException {
        Options options = Arguments.getOptions();
        DefaultParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args, null, false);
        return new Arguments(cmd);
    }

    public static Options getOptions() {
        Options options = new Options();
        options.addOption(
                Option.builder("g")
                        .longOpt(GRAPHICS_PARAM)
                        .argName("GRAPHICS")
                        .desc("Training with graphics")
                        .build());
        options.addOption(
                Option.builder("b")
                        .longOpt(BATCH_SIZE_PARAM)
                        .hasArg()
                        .argName("BATCH-SIZE")
                        .desc("The batch size of the training data.")
                        .build());
        options.addOption(
                Option.builder("p")
                        .longOpt(PRE_TRAINED_PARAM)
                        .argName("PRE-TRAINED")
                        .desc("Use pre-trained weights")
                        .build());
        options.addOption(
                Option.builder("t")
                        .longOpt(TESTING_PARAM)
                        .argName("TESTING")
                        .desc("test the trained model")
                        .build());
        return options;
    }
}
