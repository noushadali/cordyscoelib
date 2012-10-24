package com.cordys.coe.util.cmdline;

/**
 * Holds the Class TestPowershell.
 */
public class TestPowershell
{
    /**
     * Main method.
     *
     * @param saArguments Commandline arguments.
     */
    public static void main(String[] saArguments)
    {
        try
        {
            CmdLine cmd = new CmdLine("C:/Windows/System32/WindowsPowerShell/v1.0/powershell.exe");
            cmd.addArgument("ExecutionPolicy");
            cmd.addArgument("Unrestricted");
            cmd.addArgument("-NonInteractive");
            cmd.addArgument("-File");
            cmd.addArgument("./test/java/com/cordys/coe/util/cmdline/test.ps1");
            cmd.addArgument("-i");
            cmd.addArgument("aaaa");
            cmd.addArgument("-o");
            cmd.addArgument("bbbb");
            
            cmd.setFailOnStdErr(false);
            
            cmd.setCloseStdIn(true);
            
            int retVal = cmd.execute();
            
            System.out.println("RetVal: " + retVal);
            
            System.out.println("StdOut: " + cmd.getStdOut());
            System.out.println("StdErr: " + cmd.getStdErr());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
