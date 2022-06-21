/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;



import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


/**
 * This class propagates flow according to the link transmission model.
 *
 * @author Te Xu changed MWL's LTM code for APWBP paper
 */

// TODO: 根据Michael & Yash 的代码 调整目前代码的结构
public class LTM extends Link
{

    // private LinkedList<Double> demand; // 我们可能不需要 demand 这个变量

    private double yin;
    private double yout;

    private LinkedList<Double> N_up; // time dependent??
    private LinkedList<Double> N_down; // time dependent??  //N_down is a fake point that very close to the downstream point

    private double x_s;

    // w 需要自己列公式算

    public LTM(int id, Node source, Node dest, double length, double ffspd, double capacityPerLane, int numLanes)
    {
        super(id, "LTM", source, dest, length, ffspd, capacityPerLane, numLanes); // where is the backwardspeed ???

        reset();

    }

    public LinkedList<Double> getN_up() {
        return N_up;
    }

    public LinkedList<Double> getN_down() {
        return N_down;
    }

    public void reset()
    {



        N_up = new LinkedList<Double>();
        N_down = new LinkedList<Double>();


        for (int i = 0; i < Math.round(getLength()/(getFFSpeed()/3600)/Params.dt); i++ )
        {
            N_up.add(0.0);

        }

        //int size1;
        //size1 = N_up.size();
        //System.out.println(size1); // ??? 看不到打印出来的文字 ---> 在log.txt 里面

        double w = getCapacityPerLane()/ Params.JAM_DENSITY; // 这个问非常 不确定？？？ 这个感觉是错的！！！

        for (int i = 0; i < Math.round(getLength() / (w / 3600) /Params.dt); i++ )
        {
            N_down.add(0.0);

        }

        //int size2;
        //size2 = N_down.size();
        //System.out.println("downstream_N_size" + size2 + " "); // ??? 看不到打印出来的文字

        // Initialize the value

        yin = 0;
        yout = 0;


    }


    public int get_upSize() {
        int size1;
        size1 = N_up.size();

        return size1;
    }


    public int get_downSize() {
        int size2;
        size2 = N_down.size();

        return size2;

    }



    public double getOccupancy() // is it tracks number of vehilces on the link???
    {
        // fill this in

        return N_up.getLast()- N_down.getLast();  // this is correct


    }

    public void step()
    {
        // fill this in


    }

    public void update()
    {
        // 我的写法： 也是对的 ？？？why ？？？
        // N_up.add(yin);
        // N_up.removeFirst();

        // N_down.add(yout);
        // N_down.removeFirst();


        ///*   // 答案写法

        double N_last = N_up.getLast(); // 按数据存储的顺序来看 last 位置是 当前timestep t 存储的数据
        N_up.add(N_last + yin);

        N_last = N_down.getLast();
        N_down.add(N_last + yout);

        yin = 0; // why??
        yout = 0; // why??

        N_up.removeFirst();
        N_down.removeFirst();
        // */
    }




    public double getSendingFlow()  // min{ N(t+dt-l/uf, 0), Q*dt }
    {
        // fill this in


        return  Math.min(N_up.getFirst()- N_down.getLast(), getCapacity());

    }

    @Override
    public double getN() {
        return N_up.getFirst()- N_down.getLast();
    }

    public double getReceivingFlow() // min{ N(t+dt-l/w, l), Q*dt }  // 注意这个和计算 shockwave位置的function是不一样的，因为这个是考虑了整个路段长度length的
    {                                //  N_up(t - x_s/uf) = N_down(t - (L-x_s)/wb) + Kj * (L - x_s)
        // fill this in

        return  Math.min(N_down.getFirst() - N_up.getLast() + Params.JAM_DENSITY * this.getNumLanes() * getLength(), getCapacity() * Params.dt/3600.0);
    }

    /*
     * yin yout 与 R(t) S(t) 有什么区别 联系？回忆一下定义。
     */

    public void addFlow(double y) // flow from all incoming links
    {
        // fill this in
        yin += y;
        logEnteringFlow(y);
    }

    @Override
    public double getPressure(Link downstreamLink, double turningProportion) {
        // TODO: add case when this link is EntryLink and downstreamLink is LTM
        if (downstreamLink instanceof LTM dl) {
            // TODO: Set params C_ab, C_bc, Pi_bc
            double C_ab = 0;
            double C_bc = 0;
            double Pi_bc = 0;

            return LTM.getPressure_normal(this, dl, C_ab, C_bc, Pi_bc, Params.time, Params.time,
                    this.getN_up(), dl.getN_up(), this.getN_down(), dl.getN_down());

        }

        // TODO: what is the other case??
        return 0.0;
    }

    public void removeFlow(double y) // flow from all outgoing links
    {
        // fill this in
        yout += y;

    }







    // 下面这些记录有很多需要讨论的点


    // 本方程是用于算出 x_s 的位置的
    int cur_time; // ---> cur_time 应该就是仿真进行的 时刻 Params.time
    // ??? if cur_time = Params.time 那就是算出 仿真过程中 所有time step的 x_s；
    // 如果 cur_time 是一个任意值 那就是算出给定任意时刻 仿真过程中的 x_s；---> 这个思路不对，因为 N_up and N_down 的 size 会不断的迭代更新
    // 如果 cur_time 不是int 呢？？？ 例如 cur_time = 8.5 time steps, 有没有这个可能？ 我感觉可能没必要

    // Jam_density = ?;
    // uf = ?;

    // N_up = <>;
    // N_down = <>;


    // 这个代码结构测不出 Multiple ShockWave
    /*
    public LinkedList<Double> ShockWaveDetection(int cur_time)
    {
    	 // x_s is the solution, we do not know x_s
    	 // N_up(t - x_s/uf) = N_down(t - (L-x_s)/wb) + Kj * (L - x_s)


    	LinkedList<Double> correct_xs = new LinkedList<Double>();


    	// 改正：不能是 N_up.size() - 1 & N_down.size() - 1; ---> Size 大小影响到你能否trace back to correct answers
    	// if L/uf = 4, L/wb = 8, N_up.size() = 5 index =[0, 1, 2, 3, 4]; N_down.size() = 9, index = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]

    	// 这意味着 N_up & N_down 的 initialization 需要改
    	// 先假设已经改好了 Hard Code 完以后再测试
    	for (int i = 0; i < N_up.size() - 1; i++) {
    		for (int j = 0; j < N_down.size() - 1; j++) {

    			// w wb uf unit 需要注意 脑子清醒的时候算算对不对；现在先不管这个
    			// 注意 代码单位是 per dt 而输入 是 mile per hour
    			double w =  getCapacityPerLane()/ Params.JAM_DENSITY; // 单位 per dt
    			double wb = (w / 3600) /Params.dt; // 单位 per dt
    			double uf = (getFFSpeed()/3600)/Params.dt; // 单位 per dt

    			// 写成两个b 方便debug
    			double b1 = (wb*uf)* N_up.get(i) + ((wb*uf)*(cur_time) - (wb*uf)*(Params.time - (N_up.size() - (i+1))))*(N_up.get(i+1) - N_up.get(i));
    			double b2 = (wb*uf)* N_down.get(j) + ((wb*uf)*(cur_time) - uf*getLength() - (wb*uf)*(Params.time - (N_down.size() - (j+1))))*(N_down.get(j+1) - N_down.get(j)) + wb*uf*Params.JAM_DENSITY*getLength();
    			double b = b1 - b2;


    			double a  = (uf*(N_down.get(j+1) - N_down.get(j)) - wb*uf*Params.JAM_DENSITY + wb*(N_up.get(i+1) - N_up.get(i)));


    		    double temp_xs;
    		    temp_xs = SolveLinearEquation(a, b);  // ---> 搞清楚：多个 temp_xs 的值来自于 loop；而 LinearEquation() 一次只能求出一个 temp_xs 的解


    		    // using if to check temp_xs 是否有效 ---> 看手算笔记
    		    double up_stream_time_check;
    		    double down_stream_time_check;

    		    up_stream_time_check = cur_time - (temp_xs/uf); // ---> (t - xs / uf)
    		    down_stream_time_check = cur_time - (getLength() - temp_xs) / wb; // ---> (t - (L-x_s)/wb)

    		    int up_stream_time_lowerbound;
    		    int up_stream_time_upperbound;

    		    // check i， 是否 index 到了正确的值 ---> 看手算笔记
    		    up_stream_time_lowerbound = Params.time - (N_up.size() - (i+1));
    		    up_stream_time_upperbound = Params.time - (N_up.size() - (i+2));

    		    int down_stream_time_lowerbound;
    		    int down_stream_time_upperbound;


    		    // check j， 是否 index 到了正确的值
    		    down_stream_time_lowerbound = Params.time - (N_down.size() - (j+1)); // j
    		    down_stream_time_upperbound = Params.time - (N_down.size() - (j+2)); // j

    		    if (up_stream_time_check >= up_stream_time_lowerbound && up_stream_time_check <= up_stream_time_upperbound && down_stream_time_check >=  down_stream_time_lowerbound && down_stream_time_check <= down_stream_time_upperbound) {

    		    // 是否需要加一个 if 判断 temp_xs \in [0, Link.length]

    		    correct_xs.add(temp_xs);


    		    }


    		}

    	}


    	LinkedList<Double> x_s = correct_xs;
        return x_s; // 返回的 LinkedList 按照升序排列


    }
    */


    // 全部加一个脚标2 用于function的测试
    // Hard coding test ---> Example with --- " N_up2 = {0: 12, 1: 14, 2: 16, 3: 18, 4: 20} " --- " N_down2 = {0: 0, 1: 0, 2: 0, 3: 0, 4: 0, 5: 0, 6: 0, 7: 4, 8: 8}"
    //                          Corresponding time_up = {6      7      8      9      10   }       time_down = {2     3     4     5     6     7     8     9     10  }
    // TODO: 这个shockwaveDetection() 代码还有精度的问题需要改进 需要邮件/开会的时候 问一下MWL 或者 Yashveer

    public static LinkedList<Double> ShockWaveDetection2(int cur_time2, int params_time, LinkedList<Double> N_up2, LinkedList<Double> N_down2)
    {

        //traffic parameters
        double wb2 = 0.025; // 单位 mile/dt
        //System.out.println("wb2 is:" + wb2);
        double uf2 = 0.05; // 单位 mile/dt
        double K_jam = 240.00; // vehs/mile  ---> K值 与 时间无关
        //System.out.println("wb2*uf2 is:" + wb2*uf2);

        double L = 0.20; // 0.2 mile



        LinkedList<Double> correct_xs2 = new LinkedList<Double>();
        // 变量的初始化
        double temp_freeflow2 = 0;
        double temp_backward2 = 0;
        double temp_xs2 = 0;


        // ---> N_up flow change point checking
        // ---> Notice there would be two cases;
        // TODO: There should be more cases;

        for (int ii = 0; ii < N_up2.size() - 2; ii++) {
            // 注意这里的逻辑 和 N_down的区别
            // N_up 从有到无
            if ( N_up2.get(ii+2) - N_up2.get(ii+1) == 0 &&  N_up2.get(ii+1) - N_up2.get(ii) > 0 ) {
                //System.out.println("upstream flow change time point index is:" + (ii+1));

                // ---> downstream of this point is empty (no vehicles, please refer handouts page-17)
                temp_freeflow2 = uf2*((N_up2.size() - 1) - (ii+1)); //  速度 uf2 * 经历的时间 ((N_up2.size() - 1) - (ii+1))

                //System.out.println("Forward flow reach point is:" + temp_freeflow2);

                correct_xs2.add(temp_freeflow2);
                // N_up 从无到有
                if (N_up2.get(ii+2) - N_up2.get(ii+1) > 0 &&  N_up2.get(ii+1) - N_up2.get(ii) == 0) {
                    //System.out.println("upstream flow change time point index is:" + (ii+1));

                    // ---> upstream of this point is empty (no vehicles, please refer handouts page-17)
                    temp_freeflow2 = uf2*((N_up2.size() - 1) - (ii+1));

                    correct_xs2.add(temp_freeflow2);

                }


            }


        }



        // ---> N_down flow change point checking
        // temp_backward2 需要与 temp_xs2 比较后 才能看是否能存储下来
        for (int j =0; j < N_down2.size() - 2; j++) {
            if(( N_down2.get(j+2) - N_down2.get(j+1)) != (N_down2.get(j+1) - N_down2.get(j))) {
                //System.out.println("Downstream flow change time point index is:" + (j+1)); // ---> corresponding to LinkedList index not the real time


                temp_backward2 = L - wb2*((N_down2.size() - 1) - (j+1)); // 是 ((N_down2.size() - 1) )  和  (j+1)

                //System.out.println("Backward flow reach point is:" + temp_backward2);

                // LinkedList correct_xs2 add valid value
                correct_xs2.add(temp_backward2);

            }


        }





        for (int i = 0; i < N_up2.size() - 1; i++) {
            for (int j = 0; j < N_down2.size() - 1; j++) {


                /* 为了测试方程逻辑 有些值可以手动设置 ---> 比如 K_jam */

                //double w =  getCapacityPerLane()/ Params.JAM_DENSITY;
                //traffic parameters
                // double wb2 = 0.025; // 单位 mile/dt
                //System.out.println("wb2 is:" + wb2);
                // double uf2 = 0.05; // 单位 mile/dt
                // double K_jam = 240.00; // vehs/mile  ---> K值 与 时间无关
                //System.out.println("wb2*uf2 is:" + wb2*uf2);

                // double L = 0.20; // 0.2 mile
                //int params_time = 10;


                double b12 = (wb2*uf2)* N_up2.get(i) + ((wb2*uf2)*(cur_time2) - (wb2*uf2)*(params_time - (N_up2.size() - (i+1))))*(N_up2.get(i+1) - N_up2.get(i));  //  ---> refer 推算 04-10-2022 Page-11
                //System.out.println("b12 is:" + b12); // 手算应该是 34/800 = 0.0425 还是丢失了精度
                double b22 = (wb2*uf2)* N_down2.get(j) + ((wb2*uf2)*(cur_time2) - uf2*L - (wb2*uf2)*(params_time - (N_down2.size() - (j+1))))*(N_down2.get(j+1) - N_down2.get(j)) + wb2*uf2*K_jam*L ; //  ---> refer 推算 04-10-2022 Page-11
                //System.out.println("b22 is:" + b22); // 手算结果是 48/800 = 0.06 对应上了
                double b2 = b12 - b22;
                //System.out.println("b2 is:" + b2);  // output 都是0


                double a2  = (uf2*(N_down2.get(j+1) - N_down2.get(j)) - wb2*uf2*K_jam + wb2*(N_up2.get(i+1) - N_up2.get(i)));
                //System.out.println("a2 is:" + a2); // 把  N_down2.get(i) 改成 N_down2.get(j) 后 结果应该是对了 为 -0.25 -0.05 还有一个问题是怎么保留有效小数点




                temp_xs2 = SolveLinearEquation(a2, b2);
                //System.out.println("temp xs is:" + temp_xs2); // output 都是NaN ---> 有多少个NaN? 32个 基于 i < N_up2.size() - 1 &  j < N_down2.size() -1


                // using if to check temp_xs
                double up_stream_time_check2;
                double down_stream_time_check2;


                up_stream_time_check2 = cur_time2 - (temp_xs2/uf2); // ---> (t - xs / uf)
                down_stream_time_check2 = cur_time2 - (L - temp_xs2) / wb2; // ---> (t - (L-x_s)/wb)

                int up_stream_time_lowerbound2;
                int up_stream_time_upperbound2;

                // check i， 是否 index 到了正确的值 ---> 看手算笔记 04-10-2022 page-7
                up_stream_time_lowerbound2 = params_time - (N_up2.size() - (i+1));  // i 是 index 这里可能会有bug
                up_stream_time_upperbound2 = params_time - (N_up2.size() - (i+2));

                int down_stream_time_lowerbound2;
                int down_stream_time_upperbound2;


                // check j， 是否 index 到了正确的值
                down_stream_time_lowerbound2 = params_time - (N_down2.size() - (j+1)); // j
                down_stream_time_upperbound2 = params_time - (N_down2.size() - (j+2)); // j

                if (up_stream_time_check2 >= up_stream_time_lowerbound2 && up_stream_time_check2 <= up_stream_time_upperbound2 && down_stream_time_check2 >=  down_stream_time_lowerbound2 && down_stream_time_check2 <= down_stream_time_upperbound2) {

                    // 是否需要加一个 if 判断 temp_xs \in [0, Link.length]

                    correct_xs2.add(temp_xs2);

                }


            }

        }



        LinkedList<Double> x_s2 = correct_xs2;
        return x_s2; // 返回的 LinkedList 按照升序排列


    }





    //TODO: calculate all breakpoint & shockwave point based on shockwavedetection() function ---> Named:  ShockWaveDetection3()
    /*
     * public double shockwaveDetection();  ---> return x_shock
     *
     * public List<double> upstreamBreakpointDetection(); ---> return up_Breakpoint
     * public List<double> downstreamBreakpointDetection(); ---> return down_Breakpoint
     *
     * public shockN(double position);  ---> t-(x_s/uf) input x_shock
     * public forwardBreakN(double position); ---> flow change point index is (i+1) ---> should be multiple results
     * public backwardBreakN(double position); ---> flow change point index is (i+1) ---> should be None or One results
     * Double x_shock = shockwaveDetection();
     * List<double> x_break_downstream = downstreamBreakpointDetection();
     * List<double> x_break_upstream = upstreamBreakpointDetection();
     *
     * // split x_breaks into “x_break_downstream” and  “x_break_upstream”
     *
     * List<Double> x_break_upstream;
     * List<Double> x_break_downstream;
     * boolean hasDownstreamBreak = (x_break_downstream.size() >= 1);
     *
     * // <position, #of vehicles>
     * HashMap<double, double> position_and_vehicles;
     * position_and_vehicles.put(x_shock, )
     *
     *
     *
     */

    public static LinkedList<Double> ShockWaveDetection3(int cur_time2, int params_time, LinkedList<Double> N_up2, LinkedList<Double> N_down2)
    {
        //traffic parameters
        double wb2 = 0.025; // 单位 mile/dt
        //System.out.println("wb2 is:" + wb2);
        double uf2 = 0.05; // 单位 mile/dt
        double K_jam = 240.00; // vehs/mile  ---> K值 与 时间无关
        //System.out.println("wb2*uf2 is:" + wb2*uf2);

        double L = 0.20; // 0.2 mile

        LinkedList<Double> correct_x_shock2 = new LinkedList<Double>();
        // 变量的初始化
        //double temp_freeflow2 = 0;
        //double temp_backward2 = 0;
        double temp_xs2 = 0;


        for (int i = 0; i < N_up2.size() - 1; i++) {
            for (int j = 0; j < N_down2.size() - 1; j++) {


                double b12 = (wb2*uf2)* N_up2.get(i) + ((wb2*uf2)*(cur_time2) - (wb2*uf2)*(params_time - (N_up2.size() - (i+1))))*(N_up2.get(i+1) - N_up2.get(i));  //  ---> refer 推算 04-10-2022 Page-11
                //System.out.println("b12 is:" + b12); // 手算应该是 34/800 = 0.0425 还是丢失了精度
                double b22 = (wb2*uf2)* N_down2.get(j) + ((wb2*uf2)*(cur_time2) - uf2*L - (wb2*uf2)*(params_time - (N_down2.size() - (j+1))))*(N_down2.get(j+1) - N_down2.get(j)) + wb2*uf2*K_jam*L ; //  ---> refer 推算 04-10-2022 Page-11
                //System.out.println("b22 is:" + b22); // 手算结果是 48/800 = 0.06 对应上了
                double b2 = (b12 - b22);
                //System.out.println("b2 is:" + b2);  // output 都是0

                double a2  = (uf2*(N_down2.get(j+1) - N_down2.get(j)) - wb2*uf2*K_jam + wb2*(N_up2.get(i+1) - N_up2.get(i)));
                // System.out.println("a2 is:" + a2); // 把  N_down2.get(i) 改成 N_down2.get(j) 后 结果应该是对了 为 -0.25 -0.05 还有一个问题是怎么保留有效小数点

                temp_xs2 = SolveLinearEquation(a2, b2);
                //System.out.println("temp xs is:" + temp_xs2); // output 都是NaN ---> 有多少个NaN? 32个 基于 i < N_up2.size() - 1 &  j < N_down2.size() - 1

                // using if to check temp_xs
                double up_stream_time_check2;
                double down_stream_time_check2;

                up_stream_time_check2 = cur_time2 - (temp_xs2/uf2); // ---> (t - xs / uf)
                down_stream_time_check2 = cur_time2 - (L - temp_xs2) / wb2; // ---> (t - (L-x_s)/wb)

                int up_stream_time_lowerbound2;
                int up_stream_time_upperbound2;

                // check i， 是否 index 到了正确的值 ---> 看手算笔记 04-10-2022 page-7
                up_stream_time_lowerbound2 = params_time - (N_up2.size() - (i+1));  // i 是 index 这里可能会有bug
                up_stream_time_upperbound2 = params_time - (N_up2.size() - (i+2));

                int down_stream_time_lowerbound2;
                int down_stream_time_upperbound2;

                // check j， 是否 index 到了正确的值
                down_stream_time_lowerbound2 = params_time - (N_down2.size() - (j+1)); // j
                down_stream_time_upperbound2 = params_time - (N_down2.size() - (j+2)); // j

                if (up_stream_time_check2 >= up_stream_time_lowerbound2 && up_stream_time_check2 <= up_stream_time_upperbound2 && down_stream_time_check2 >=  down_stream_time_lowerbound2 && down_stream_time_check2 <= down_stream_time_upperbound2) {

                    correct_x_shock2.add(temp_xs2);

                }


            }

        }

        // x_s2 就是 x_shock
        //for (double item: correct_x_shock2) {
        //System.out.println("print out each item in correct_x_shock2: " + item);
        //}
        HashSet<Double> h = new HashSet<Double>(correct_x_shock2);
        LinkedList<Double> remove_duplicated_correct_x_shock2 = new LinkedList<Double>(h);
        System.out.println("After removing duplicated value in correct_x_shock2, we get single & valid x_shock value, which is: " + remove_duplicated_correct_x_shock2);

        LinkedList<Double> x_shock2 = remove_duplicated_correct_x_shock2;
        //System.out.println("we have x_shock solution, which means we meet congestion");
        return x_shock2; // 后面有个sorted function 去排序

    }


    // return x_shock corresponding x
    // TODO: 数据类型check return 应该就是double 类型
    public static LinkedList<Double> shockN(int cur_time2, LinkedList<Double> x_shock,  LinkedList<Double> N_up2, LinkedList<Double> N_down2) {
        //traffic parameters check 上游 只需要uf
        double uf2 = 0.05;
        // x_shock 的值存在了 一个 LinkedList 中
        // ！！！！！！ ------------------- 这里不能用 Math.round() 因为会把 7.2 round 成 7.0  ------------------------ ！！！！！！
        double trackback_time = (cur_time2 - (x_shock.get(0) / uf2)); // 10 - (0.14/0.05) = 7.2;
        System.out.println("trackback_time is:" + trackback_time);

        LinkedList<Double> x_shock_N = new LinkedList<Double>();
        double temp_x_shock_N = 0;

        int downbound_int_time = (int) Math.floor(trackback_time); // down_int_time = 7
        int upbound_int_time = (int) Math.ceil(trackback_time); // up_int_time = 8

        // ---> 如果 trackback_time 就是 7.00 8.00 呢？？？
        // 找一个手稿上的数值再check
        if(downbound_int_time != upbound_int_time ) {

            // cur_time = 10; index = 0, 1, 2, 3, 4
            int max_index = N_up2.size() - 1;
            int min_index = 0;

            // (difference of index) = (difference of time) 差值是 一样的
            // 参考 page-58 中的值
            int downbound_int_time_index =  max_index - (cur_time2 - downbound_int_time); // 4 - (10 -7) = 1; index = 1, 对应 time = 7 N_up(t=7) = N_up(index=1) = 14
            int upbound_int_time_index = max_index - (cur_time2 - upbound_int_time); // 4 - （10 - 8）= 4 - 2 = 2; index = 2, 对应  time = 8 N_up(t=8）= N_up(index=

            double dN = N_up2.get(upbound_int_time_index) -  N_up2.get(downbound_int_time_index);
            double dt = trackback_time - (double)downbound_int_time;

            temp_x_shock_N = (N_up2.get(downbound_int_time_index) +  dt * dN); // math.round make 14.4000000 to 14.0000000
            //temp_x_shock_N = ( N_up2.get(downbound_int_time_index) +  dt * dN);
            x_shock_N.add(temp_x_shock_N);

        } else {

            int max_index = N_up2.size() - 1;
            // if trackback_time 就是一个整数 对应一个
            temp_x_shock_N = N_up2.get(max_index -(cur_time2 - (int)trackback_time)); //return index not the time  (4 - (10 - 6))
            x_shock_N.add(temp_x_shock_N);
        }

        return x_shock_N;


    }



    // downstreamBreakpoint 需要和 x_s 比较 如果 x_breaks_upstream < x_s 则舍弃
    // TODO: check with MWL there is only one possible downstreamBreakpoint
    public static LinkedList<Double> downstreamBreakpointDetection(int cur_time2, int params_time, LinkedList<Double> N_up2, LinkedList<Double> N_down2) {
        //traffic parameters
        double wb2 = 0.025; // 单位 mile/dt
        //System.out.println("wb2 is:" + wb2);
        //double uf2 = 0.05; // 单位 mile/dt
        double K_jam = 240.00; // vehs/mile  ---> K值 与 时间无关
        //System.out.println("wb2*uf2 is:" + wb2*uf2);

        double L = 0.20; // 0.2 mile
        LinkedList<Double>  x_break_downstream = new LinkedList<Double>() ;
        // 变量的初始化
        double temp_backward2 = 0;
        int max_N_down_index = (N_down2.size() - 1);

        // ---> N_down flow change point checking
        // temp_backward2 需要与 temp_xs2 比较后 才能看是否能存储下来
        for (int j =0; j < N_down2.size() - 2; j++) {
            // TODO: 这个找backward speed breakpoint的 if 条件很重要 对于 link a
            if( (N_down2.get(j+2) - N_down2.get(j+1)) > 0 && (N_down2.get(j+1) - N_down2.get(j)) == 0 ) {
                //System.out.println("Downstream flow backward speed start point index is:" + (j+1)); // ---> corresponding to LinkedList index not the real time

                temp_backward2 = L - wb2*( max_N_down_index - (j+1)); //  L - wb * (dt)

                //System.out.println("Backward flow reach point is:" + temp_backward2);

                // LinkedList correct_xs2 add valid value
                x_break_downstream.add(temp_backward2);

            }

        }

        return  x_break_downstream; // this is a LinkedList

    }

    // get x_b_downstream index and return N_down(index) ---> 这个思路不robust 因为没有判断是否有效
    // input x_b_downstream 反算
    // TODO: N_up, N_down 是double 是否合理 ---> 回答感觉是合理的 因为 simulaiton进行是 demand 可能以非整数增加
    public static LinkedList<Double> backwardBreakN(int cur_time2, LinkedList<Double> x_break_downstream,  LinkedList<Double> N_up2, LinkedList<Double> N_down2) {

        double wb2 = 0.025;
        double K_jam = 240.00;
        double L = 0.20;
        LinkedList<Double> x_break_downstream_N = new LinkedList<Double>();
        // trackback to the break point index
        int max_N_down_index = (N_down2.size() - 1);
        // x_break_downstream 如果存在 则 只存在一个值 所以取第一个index = 0
        int backwardBreakN_index = (int) (Math.round ((x_break_downstream.get(0)  + wb2 * max_N_down_index - L)/wb2) );  // 正确使用: Math.round（包含所有计算）
        //System.out.println("value in the bracket is: " + ((x_break_downstream.get(0)  + wb2 * max_N_down_index - L)/wb2));
        //System.out.println("backwardBreakN_index is :" + backwardBreakN_index);

        double  temp_x_break_downstream_N = N_down2.get(backwardBreakN_index) + K_jam*((L - x_break_downstream.get(0)));


        x_break_downstream_N.add(temp_x_break_downstream_N);

        return x_break_downstream_N;

    }






    public static LinkedList<Double> upstreamBreakpointDetection(int cur_time2, int params_time, LinkedList<Double> N_up2, LinkedList<Double> N_down2) {
        //traffic parameters
        double wb2 = 0.025; // 单位 mile/dt
        //System.out.println("wb2 is:" + wb2);
        double uf2 = 0.05; // 单位 mile/dt
        double K_jam = 240.00; // vehs/mile  ---> K值 与 时间无关
        //System.out.println("wb2*uf2 is:" + wb2*uf2);

        double L = 0.20; // 0.2 mile

        LinkedList<Double> x_break_upstream = new LinkedList<Double>();
        double temp_freeflow2 = 0;
        double dt = 0;

        for (int ii = 0; ii < N_up2.size() - 2; ii++) {

            // 注意这里的逻辑 和 N_down的区别

            if ((N_up2.get(ii+2) - N_up2.get(ii+1)) !=  (N_up2.get(ii+1) - N_up2.get(ii))) {
                //System.out.println("upstream flow change time point index is:" + (ii+1));
                dt = ((N_up2.size() - 1) - (ii+1));
                temp_freeflow2 = uf2 * dt; //  速度 uf2 * 经历的时间 ((N_up2.size() - 1) - (ii+1))
                x_break_upstream.add(temp_freeflow2);
            }

        }

        return x_break_upstream; // This is a Linkedlist since it may have multiple values
        // 可以通过LinkedList 的 size() 来判断是否是存入有效值

    }



    // 需要一个loop loop出 LinkedList x_break_upstream 里面所有的值 然后反算index
    // TODO: x_break_upstream 里面的解还有顺序问题
    // testing data input ---> refer handwriting draft page-58
    // 这个改成 return double 是否更合适， 如果还是LinkedList 想想怎么code
    public static double forwardBreakN(int cur_time2, double single_x_break_upstream,  LinkedList<Double> N_up2, LinkedList<Double> N_down2) {
        //traffic parameters
        double wb2 = 0.025;
        double uf2 = 0.05;
        double K_jam = 240.00;
        double L = 0.20;
        double x_start = 0.00;
        int max_N_up_index = (N_up2.size() - 1);
        double dt = 0;
        double dx = 0;

        double x_break_upstream_N =  0;

        //TODO: 0.15000000000000002 输入 return 值有问题 解决精度问题 先用多余的弄了
        // 精度问题
        dx = single_x_break_upstream - x_start;
        //System.out.println("dx ----> " + dx);
        dt = dx / uf2;
        //System.out.println("dt ----> " + dt);

        // track back to index
        int temp_index = (int)(Math.round(max_N_up_index - dt)); // Math.round (5.9999999999999964) = 5.0 Math.round (6.0000000000001) = 6.0
        //System.out.println("(max_N_up_index - dt) is:" + (max_N_up_index - dt));
        //System.out.println("temp_index is:" + temp_index);
        double temp_x_break_upstream_corresponding_N = N_up2.get(temp_index);

        x_break_upstream_N = temp_x_break_upstream_corresponding_N;
        return x_break_upstream_N;

    }









    // @Params: Input is: N_up and N_down
    // make this work first then code getDensity() & calculatePressure()
    // TODO: Testing this code with Handwriting draft
    public static HashMap<Double, Double> get_X_N_pairs(int cur_time2, int params_time, LinkedList<Double> N_up2, LinkedList<Double> N_down2) {

        // at each simulation time step
        // step-1: get all possible "x" boundary for a link: x_start, x_break_upstream1, x_break_upstream2, x_shock, x_break_downstream, x_end
        // Step-2: if we have x_break_downstream.size() >= 1 ---> boolean hasDownstreamBreak = (x_b_downstream.size() >= 1)
        // Then we must have x_shock and x_break_downstream, and density between them, which is K_jam
        // Otherwise, we may only have x_break_upstream or x_shock

        // step-3: get all X <---> N pair and store them as a HashMap
        // step-4: start calculating density

        // 从 x_start 到 x_end 顺序 put key & value pairs
        HashMap<Double, Double> X_N_pairs = new HashMap<Double, Double>();

        double x_start = 0;
        double x_end = 0.2; // ---> later change to get link length
        int max_N_up_index = N_up2.size() - 1;
        int min_N_up_index = 0;
        int max_N_down_index = N_down2.size() - 1;;
        int min_N_down_index = 0;

        double x_start_N = N_up2.get(max_N_up_index);
        double x_end_N = N_down2.get(max_N_down_index);

        // 顺序1 x_start
        X_N_pairs.put(x_start, x_start_N);

        // 顺序2 x_break_upstream
        LinkedList<Double> x_break_upstream = upstreamBreakpointDetection(cur_time2, params_time, N_up2, N_down2);

        if (x_break_upstream.size() >  0) {
            // for each x_break_upstream get corresponding N

            for (int i = 0; i <= x_break_upstream.size() - 1; i++) {
                double single_x_break_upstream = x_break_upstream.get(i);
                System.out.println("we have upstream break boundary point, the value is: " + single_x_break_upstream);
                double temp_x_break_upstream_N = forwardBreakN(cur_time2, single_x_break_upstream, N_up2, N_down2);
                X_N_pairs.put(single_x_break_upstream, temp_x_break_upstream_N);
            }

        } else {
            System.out.println("we do not have upstream break boundary point");
        }


        //顺序3 x_shock
        LinkedList<Double> x_shock = ShockWaveDetection3(cur_time2, params_time, N_up2, N_down2);
        System.out.println("we have x_shock solution, value is" + x_shock);
        // check 是否有 shockwave
        // TODO: x_shock solution 可能会是 link length 的长度 AKA 的 link 的 endpoint 这个点要 remove ？？？？ 但是有没有更好的的方法 下次开会讨论
        // TODO: 算 t = 15 link b case的时候 x_break_upstream 可能会 > x_shock 这是不符合实际的 也应该剔除
        if (x_shock.size() > 0 && x_shock.get(0)!= x_end) {
            LinkedList<Double> x_shock_N = shockN(cur_time2, x_shock, N_up2, N_down2);
            System.out.println("We have x_shock solution, which means we face congestion, and x_shock position is: " + x_shock + " and correspoding # of vehicles are" + x_shock_N + "!!!!! and we will test whether we have backward boundary");
            // x_shock is a linkedlist we need loop them to get each value
            // However, since at each time for each link we have only one x_shock and x_shock_N
            X_N_pairs.put(x_shock.get(0), x_shock_N.get(0));

            // 想确认是否有 x_break_upstream 的值
            if (x_break_upstream.size() > 0) {
                for (int i = 0; i <= x_break_upstream.size() - 1; i++) {
                    double single_x_break_upstream = x_break_upstream.get(i);
                    System.out.println("we have upstream break boundary point, since we also have x_shock point, we need to compare them. If x_break_upstream > x_shock, we need to remove x_break_upstream ");
                    if (single_x_break_upstream >  x_shock.get(0)) {
                        System.out.println(" Yes !!!!! We find some in-valid x_break_upstream point");
                        // have map remove key-value pairs
                        X_N_pairs.remove(single_x_break_upstream);
                    }

                }

            }



        } else {
            System.out.println("we do not have shockwave, so we don't have backward boundary either && We do not need to check x_shock and and x_break_upstream");
        }



        // 顺序4 x_break_downstream
        // check 是否有 shockwave 和 backward boundary 同时存在
        // if (backward boundary > shockwave) save both
        // Otherwise, only save shockwave
        LinkedList<Double> x_break_downstream = downstreamBreakpointDetection(cur_time2, params_time, N_up2, N_down2);
        System.out.println("we have x_break_downstream solution, value is" + x_break_downstream);
        // Notice --->  x_break_downstream value > x_shock value 才认为 这个 x_break_downstream 是有效值
        if (x_shock.size() > 0 && x_break_downstream.size() > 0 && x_break_downstream.get(0) > x_shock.get(0)) {
            LinkedList<Double> x_break_downstream_N =  backwardBreakN(cur_time2, x_break_downstream, N_up2, N_down2);
            System.out.println("We have valid x_shock solution and x_break_downstream solution, so we will save them");
            X_N_pairs.put(x_break_downstream.get(0), x_break_downstream_N.get(0));
        } else {
            if (x_break_downstream.size() > 0) {
                System.out.println("This is an invalid x_break_downstream solution since x_break_donwstream < x_shock ");
            } else {
                System.out.println("We don't have backward boundray AKA !downstreamBreakpoint!");
            }
        }


        //顺序5: x_end
        X_N_pairs.put(x_end, x_end_N);


        //TODO: How to sort HashMap according the double keys value ??? We need a sorted one to calculate density
        return sortbykey(X_N_pairs);


    }


    // TODO: keySet() index 的顺序 和 打印出来的 List of X_N_pairs的顺序 可能跟直观认为的不太一样; 一定要多check 几个逻辑才能决定
    public static HashMap<Double, Double> sortbykey(HashMap<Double, Double> map)
    {
        ArrayList<Double> sortedKeys = new ArrayList<Double>(map.keySet());
        HashMap<Double, Double> sorted_X_N_pairs = new HashMap<>();

        // 上面是 x_end: 0.2 mile 下面是 x_start: 0.0 mile
        // reverse 是反转 不是降序
        // TODO: check this 易错
        Collections.sort(sortedKeys, Collections.reverseOrder());
        // Collections.sort(sortedKeys);

        // Display the TreeMap which is naturally sorted
        for (double x : sortedKeys) {
            sorted_X_N_pairs.put(x, map.get(x));
            System.out.println("Key = " + x + ", Value = " + map.get(x));
        }

        return sorted_X_N_pairs;

    }


    // TODO: getDensity (map = sorted_X_N_pairs) 一定要是 排序过的 因为 计算的 density 的逻辑是有顺序的
    // Idea: checking if ( HashMap.get(Keysets[i])  = x_shock && x_break_downstream.size() > 0 && x_break_downstream.get(0) > x_shock.get(0)) we find k_jam position we return this position for index to get density and to calculate pressure)

    // Input: HashMap sorted_X_N_pairs
    // Return: density along the link
    // TODO: checking some printout especially the order； checking the loop index size, maybe some small errors.
    public static ArrayList<Double> getDensity(HashMap <Double, Double> sorted_X_N_pairs, int cur_time2, int params_time, LinkedList<Double> N_up2, LinkedList<Double> N_down2){
        // 若存在 他们的 size() = 1; 有且只有一个;
        double K_jam = 240.00;
        //LinkedList<Double> temp_x_shock_forchecking = ShockWaveDetection3(cur_time2, params_time, N_up2, N_down2);
        //LinkedList<Double> temp_x_break_downstream_forchecking = downstreamBreakpointDetection(cur_time2, params_time, N_up2, N_down2);

        int linkDensitysize = sorted_X_N_pairs.size() - 1; // 以 link b & time = 17 为例 density size 是 X_N_pairs size() - 1 = 5 - 1 = 4
        ArrayList<Double> linkDensity = new ArrayList<>(linkDensitysize);

        //int jam_density_lowindex = 0;
        //int jam_density_upindex = 0;

        ArrayList<Double> sortedKeys = new ArrayList<Double>(sorted_X_N_pairs.keySet());
        Collections.sort(sortedKeys);
        //Collections.sort(sortedKeys, Collections.reverseOrder());
        List<Double> indexes = new ArrayList<Double>(sortedKeys); // --> Notice: List of sorted_X_N_pairs indexes is: [0.0, 0.15, 0.1, 0.2] if we use Collections.sort(sortedKeys);
        System.out.println("sorted X_N_pairs indexes List is:" + indexes);

        double temp_Density;
        double dN;
        double dX;

        for (int i = 0; i < sorted_X_N_pairs.size() - 1; i++) {
            dN = sorted_X_N_pairs.get(indexes.get(i)) -  sorted_X_N_pairs.get(indexes.get(i+1)); // i < 4 ; i = 3 i+1 = 4 这个是index 是对的
            dX = indexes.get(i+1) - indexes.get(i);
            temp_Density = dN/dX;
            linkDensity.add(temp_Density);
        }


        // ---------------------------------------------------------------------------------------------------------------------------------------------------------------- //
        // if x_b x_s 存在 我们希望return 一个 index for sorted_X_N_pairs
    	/*
        if(temp_x_shock_forchecking.size() > 0 && temp_x_break_downstream_forchecking.size() > 0 && temp_x_break_downstream_forchecking.get(0) > temp_x_shock_forchecking.get(0)) {
    	   System.out.println("We have valid x_shock solution and x_break_downstream solution, so we have jam_density !!!");
    	   System.out.println("We start to calculate cases with K_jam");
    	   // return the jam density corresponding index
    	   if (sorted_X_N_pairs.containsKey(temp_x_shock_forchecking.get(0)) == true && sorted_X_N_pairs.containsKey(temp_x_break_downstream_forchecking.get(0)) == true){
    		   jam_density_lowindex = indexes.indexOf(temp_x_shock_forchecking.get(0));
    		   jam_density_upindex = indexes.indexOf(temp_x_break_downstream_forchecking.get(0));
    		   }
    	   // case 1 calculate density
    	   for (int i = 0; i <= jam_density_lowindex - 1; i++ ) {
    		   dN =  sorted_X_N_pairs.get(indexes.get(i)) -  sorted_X_N_pairs.get(indexes.get(i+1)); // i+1 =  0+1 = 1
    		   dX =  indexes.get(i+1) - indexes.get(i);
    		   temp_Density = dN / dX;
    		   linkDensity.add(temp_Density);

    	   }

    	   for (int i = jam_density_lowindex; i < jam_density_upindex; i++) {
    		   // add jam density
    		   // TODO: 这个肯定有其他的方法 再仔细想想 我觉得最好问 MWL
    		   linkDensity.add(K_jam);
    	   }
    	   // sorted_X_N_pairs.size() - 1 = 4-1 = 3
    	   // jam_density_upindex = 2; i < 3; i = 2
    	   for( int i = jam_density_upindex; i < sorted_X_N_pairs.size() - 1; i++) {
    		   // 这里应该是 n_down(当前time index) - n_up(break point index);
    		   // TODO: 这个绝对不是最正确的解 要仔细想想 再问 MWL！！！
    		   dN =  sorted_X_N_pairs.get(indexes.get(i)) -  sorted_X_N_pairs.get(indexes.get(i+1));
    		   dX =  indexes.get(i+1) - indexes.get(i);
    		   temp_Density = dN / dX;
    		   linkDensity.add(temp_Density);
    	   }


    	   }
    		else {
    			System.out.println("We do not have valid x_shock solution and x_break_downstream solution, so we do not have jam_density !!!");
    			System.out.println("We start to calculate cases without K_jam");
    			// case 2  calculate density
    			// assume linkDensitysize = 3, then sorted_X_N_pairs.size() = 4
    			// i = 0, 1, 2; i+1 = 1, 2, 3
    			// get_X_N_pairs 已经确定了 对应 N_up N_down 的值 如果要check 从这里开始
    			for (int i = 0; i <= linkDensitysize; i++) {
    				dN =  sorted_X_N_pairs.get(indexes.get(i)) -  sorted_X_N_pairs.get(indexes.get(i+1));
    				dX =  indexes.get(i+1) - indexes.get(i);
    				temp_Density = dN / dX; // 	确保density_value的正负号逻辑
    				linkDensity.add(temp_Density);

    			}



    		}
        */
        // ---------------------------------------------------------------------------------------------------------------------------------------------------------------- //
        return linkDensity;

    }





    // pressure 是一个double 还是一个 int
    // Implement Jabari's pressure calculation method based on  (1)---a-->(2)------b----->(3)------c----->(4)-d->  a is source link; b,c is normal link, vehicle leave network from d
    // how to update time in code
    // 先写一个demo 然后问Yashveer 去优化 pressure 是基于movement的  因此基于PQ3 network 看需要怎么用 ---> 问他写完了 mp pq3 吗？？？
    // upstream link is a soucre link or we call
    public static double getPressure_upSource(EntryLink upLink, LTM downLink, double TurningPorp, int cur_time2, LinkedList<Double> N_up2, LinkedList<Double> N_down2) {

        double downLength = downLink.getLength();
        //LinkedList<Double> current_xs = downLink.ShockWaveDetection2(cur_time2, cur_time, try_N_up, try_N_down);
        // sort the value in current_xs linkedlist from small to large
        // Collections.sort(current_xs);
        // loop and print out these value
        // if there is 2 xs solution, which means we have 3 density, but how I know these denstiy ??? Also, when we find a method to solve these density, we should store them
        // unit is veh/mile
        // 就是问他 怎么keep fill in and read out these value

        double density_srcLink = 40;
        double density_1 = 40;
        double density_2 = 240;
        double denstiy_3 = 80;

        // 根据density 计算积分


        return 0;
    }


    // upstream link a is not source link or we call entry link
    // Notice 是取绝对值！！！
    // TODO: c_ab c_bc constant setting ???
    public static double getPressure_normal(LTM upLink, LTM downLink, double C_ab, double C_bc, double Pi_bc, int cur_time2, int params_time, LinkedList<Double> N_up2_upLink, LinkedList<Double> N_up2_downLink, LinkedList<Double> N_down2_upLink, LinkedList<Double> N_down2_downLink) {

        double upLink_length = 0.2; // change this to upLink.getLength() when simulation start
        double downLink_length = 0.2; // change this to downLink.getLength() when simulation start

        //int linkDensitysize = sorted_X_N_pairs.size() - 1;
        //ArrayList<Double> linkDensity = new ArrayList<>(linkDensitysize);

        HashMap<Double, Double> sorted_X_N_pairs_upLink = LTM.get_X_N_pairs(cur_time2, params_time, N_up2_upLink, N_down2_upLink);
        HashMap<Double, Double> sorted_X_N_pairs_downLink = LTM.get_X_N_pairs(cur_time2, params_time, N_up2_downLink, N_down2_downLink);

        // for upLink

        ArrayList<Double> sortedKeys_upLink = new ArrayList<Double>(sorted_X_N_pairs_upLink.keySet());
        Collections.sort(sortedKeys_upLink);
        //Collections.sort(sortedKeys, Collections.reverseOrder());
        List<Double> indexes_upLink = new ArrayList<Double>(sortedKeys_upLink); // --> Notice: List of sorted_X_N_pairs indexes is: [0.0, 0.15, 0.1, 0.2] if we use Collections.sort(sortedKeys);
        System.out.println("sorted X_N_pairs indexes for upstream Link (link_a in jabari's paper) List is >>>>>> " + indexes_upLink);

        double temp_Density_upLink;
        double dN_upLink;
        double dX_upLink;
        double temp_Intergration_upLink;
        double upLink_part_value = 0;
        // Math.pow(a,b) -> a的b次方 不是 Math.sqrt() !!!!!
        for (int i = 0; i < sorted_X_N_pairs_upLink.size() - 1; i++) {
            dN_upLink = sorted_X_N_pairs_upLink.get(indexes_upLink.get(i)) -  sorted_X_N_pairs_upLink.get(indexes_upLink.get(i+1)); // i < 4 ; i = 3 i+1 = 4 这个是index 是对的
            dX_upLink = indexes_upLink.get(i+1) - indexes_upLink.get(i);
            temp_Density_upLink = dN_upLink/dX_upLink;
            System.out.println("temp_Density_upLink ======> " + temp_Density_upLink );
            System.out.println(" x ======> " + indexes_upLink.get(i+1));
            System.out.println(" x ======> " + indexes_upLink.get(i));
            System.out.println(" x^2 ======> " + Math.pow(indexes_upLink.get(i+1), 2) );
            System.out.println(" x^2 ======> " + Math.pow(indexes_upLink.get(i), 2) );

            temp_Intergration_upLink = (0.5)* Math.pow(indexes_upLink.get(i+1), 2) - (0.5)* Math.pow(indexes_upLink.get(i), 2);
            System.out.println("temp_Intergration_upLink ======> " + temp_Intergration_upLink);
            //linkDensity.add(temp_Density_upLink);
            System.out.println("temp_Density_upLink * temp_Intergration_upLink ======> " + (temp_Density_upLink * temp_Intergration_upLink));
            System.out.println("C_ab * (1/la) ======> " + (C_ab)*(1/upLink_length));
            upLink_part_value += (C_ab)*(1/upLink_length)*(temp_Density_upLink * temp_Intergration_upLink);

            System.out.println("upLink_part_value ======> " + upLink_part_value );
        }


        // for downLink

        ArrayList<Double> sortedKeys_downLink = new ArrayList<Double>(sorted_X_N_pairs_downLink.keySet());
        Collections.sort(sortedKeys_downLink);
        //Collections.sort(sortedKeys, Collections.reverseOrder());
        List<Double> indexes_downLink = new ArrayList<Double>(sortedKeys_downLink); // --> Notice: List of sorted_X_N_pairs indexes is: [0.0, 0.15, 0.1, 0.2] if we use Collections.sort(sortedKeys);
        System.out.println("sorted X_N_pairs indexes for downstream Link (link_b in jabari's paper) List is >>>>>> " + indexes_downLink);


        double temp_Density_downLink;
        double dN_downLink;
        double dX_downLink;
        double temp_Intergration_downLink1;
        double temp_Intergration_downLink2;
        double downLink_part_value = 0;


        for (int i = 0; i < sorted_X_N_pairs_downLink.size() - 1; i++) {
            dN_downLink = sorted_X_N_pairs_downLink.get(indexes_downLink.get(i)) -  sorted_X_N_pairs_downLink.get(indexes_downLink.get(i+1)); ;
            dX_downLink = indexes_downLink.get(i+1) - indexes_downLink.get(i);
            temp_Density_downLink = dN_downLink/dX_downLink;
            System.out.println("temp_Density_downLink ======> " + temp_Density_downLink );
            temp_Intergration_downLink1 = temp_Density_downLink * (indexes_downLink.get(i+1) - indexes_downLink.get(i));
            System.out.println("temp_Intergration_downLink1 ======> " + temp_Intergration_downLink1 );
            //System.out.println("temp_Density_downLink * (1 / downLink_length)======> " +temp_Density_downLink * (1 / downLink_length) );
            //System.out.println("((0.5)* Math.pow(indexes_downLink.get(i+1), 2) - (0.5)* Math.pow(indexes_downLink.get(i), 2)) ======> " + ((0.5)* Math.pow(indexes_downLink.get(i+1), 2) - (0.5)* Math.pow(indexes_downLink.get(i), 2)));
            temp_Intergration_downLink2 =  temp_Density_downLink * (1 / downLink_length) * ((0.5)* Math.pow(indexes_downLink.get(i+1), 2) - (0.5)* Math.pow(indexes_downLink.get(i), 2));
            System.out.println("temp_Intergration_downLink2 ======> " + temp_Intergration_downLink2);
            downLink_part_value +=  C_bc * Pi_bc * (temp_Intergration_downLink1 - temp_Intergration_downLink2);


        }

        System.out.println("The pressure value for upLink a calculated from my paper APWBP is ------>" + upLink_part_value);
        System.out.println("The pressure value for downLink a calculated from my paper APWBP is ------>" + downLink_part_value);

        double pressure_Value = Math.abs(upLink_part_value - downLink_part_value);
        System.out.println("The pressure value calculated from my paper APWBP is ------>" + pressure_Value);



        return pressure_Value ;
    }


    /// TODO: combing two type of pressure calculation function




    // 有没有更加聪明的方法
    // 如果没有 static JAVA 会报错 让你 change the method to "static"
    public static double SolveLinearEquation(double a, double b)  // ax + b = 0; this is the formulation of general linear equation
    {
        double sol;

        sol = (b / a); //

        return sol;
    }


    // try to call a specific function Hard Code Testing 的时候用!!!
    /*
    public static void main(String[] args) {

    	double try_result = LTM.SolveLinearEquation(10, 2);

    	System.out.println("The output is:" + try_result);



    }
    */

    // Hard coding test ---> Example with --- " N_up2 = {0: 12, 1: 14, 2: 16, 3: 18, 4: 20} " --- " N_down2 = {0: 0, 1: 0, 2: 0, 3: 0, 4: 0, 5: 0, 6: 0, 7: 4, 8: 8}"
    //                          Corresponding time_up = {6      7      8      9      10   }       time_down = {2     3     4     5     6     7     8     9     10  }


    public static void main(String[] args) {

        /*-------------------------------------------------------------------------------------------------*/
        // manually create N_up
        LinkedList<Double> try_N_up = new LinkedList<Double>();

        // link a cur_time = 2 params_time = 2
    	/*
    	try_N_up.add((double) 0);
    	try_N_up.add((double) 0);
    	try_N_up.add((double) 0);
    	try_N_up.add((double) 2);
    	try_N_up.add((double) 4);
    	*/

        // link a cur_time = 5 params_time = 5 normal condition
    	/*
    	try_N_up.add((double) 2);
    	try_N_up.add((double) 4);
    	try_N_up.add((double) 6);
    	try_N_up.add((double) 8);
    	try_N_up.add((double) 10);
    	*/


        // link a cur_time = 5 params_time = 5 two break point condition checking page-57
    	/*
    	try_N_up.add((double) 2);
    	try_N_up.add((double) 4);
    	try_N_up.add((double) 7);
    	try_N_up.add((double) 10);
    	try_N_up.add((double) 14);
    	*/


        // link a cur_time = 8 params_time = 8
    	/*
    	try_N_up.add((double) 8);
    	try_N_up.add((double) 10);
    	try_N_up.add((double) 12);
    	try_N_up.add((double) 14);
    	try_N_up.add((double) 16);
    	*/


        // link a cur_time = 9 params_time = 9
        /*
    	try_N_up.add((double) 10);
    	try_N_up.add((double) 12);
    	try_N_up.add((double) 14);
    	try_N_up.add((double) 16);
    	try_N_up.add((double) 18);
    	*/



        // link a cur_time = 10 params_time = 10
    	/*
    	try_N_up.add((double) 12); // 6
    	try_N_up.add((double) 14); // 7
    	try_N_up.add((double) 16); // 8
    	try_N_up.add((double) 18); // 9
    	try_N_up.add((double) 20); // 10
    	*/


        // link a cur_time = 11 params_time = 11
    	/*
    	try_N_up.add((double) 14); // 7
    	try_N_up.add((double) 16); // 8
    	try_N_up.add((double) 18); // 9
    	try_N_up.add((double) 20); // 10
    	try_N_up.add((double) 22); // 11
    	*/

        // link a cur_time = 12 params_time = 12
    	/*
    	try_N_up.add((double) 16); // 8
    	try_N_up.add((double) 18); // 9
    	try_N_up.add((double) 20); // 10
    	try_N_up.add((double) 22); // 11
    	try_N_up.add((double) 24); // 12
    	*/

        // link a cur_time = 13 params_time = 13
    	/*
    	try_N_up.add((double) 18); // 9
    	try_N_up.add((double) 20); // 10
    	try_N_up.add((double) 22); // 11
    	try_N_up.add((double) 24); // 12
    	try_N_up.add((double) 26); // 13
    	*/


        // ---> for link b
        // link b cur_time = 13 params_time = 13
    	/*
    	try_N_up.add((double) 4); // 9
    	try_N_up.add((double) 8); // 10
    	try_N_up.add((double) 12); // 11
    	try_N_up.add((double) 16); // 12
    	try_N_up.add((double) 16); // 13
    	*/


        // link b cur_time = 15 params_time = 15
    	/*
    	try_N_up.add((double) 12); // 11
    	try_N_up.add((double) 16); // 12
    	try_N_up.add((double) 16); // 13
    	try_N_up.add((double) 16); // 14
    	try_N_up.add((double) 16); // 15
    	*/

        // link b cur_time = 17 params_time = 17
    	/*
    	try_N_up.add((double) 16); // 13
    	try_N_up.add((double) 16); // 14
    	try_N_up.add((double) 16); // 15
    	try_N_up.add((double) 16); // 16
    	try_N_up.add((double) 20); // 17
    	*/
        // -------------------------------------- getPressure() testing N_up for link a & link b -------------------------------------- //
        LinkedList<Double> try_N_up_linka = new LinkedList<Double>();
        LinkedList<Double> try_N_up_linkb = new LinkedList<Double>();

        // link a & link b cur_time = 11 params_time = 11 normal condition

        try_N_up_linka.add((double) 14);
        try_N_up_linka.add((double) 16);
        try_N_up_linka.add((double) 18);
        try_N_up_linka.add((double) 20);
        try_N_up_linka.add((double) 22);

        try_N_up_linkb.add((double) 0);
        try_N_up_linkb.add((double) 0);
        try_N_up_linkb.add((double) 4);
        try_N_up_linkb.add((double) 8);
        try_N_up_linkb.add((double) 12);




        // link a & link b cur_time = 13 params_time = 13 normal condition
    	/*
		try_N_up_linka.add((double) 18);
    	try_N_up_linka.add((double) 20);
    	try_N_up_linka.add((double) 22);
    	try_N_up_linka.add((double) 24);
    	try_N_up_linka.add((double) 26);

		try_N_up_linkb.add((double) 4);  //9
    	try_N_up_linkb.add((double) 8);  //10
    	try_N_up_linkb.add((double) 12); //11
    	try_N_up_linkb.add((double) 16); //12
    	try_N_up_linkb.add((double) 16); //13
    	*/







        System.out.println("N_up is:" + try_N_up);
        System.out.println("N_up size is: " + try_N_up.size());


        /*-------------------------------------------------------------------------------------------------*/

        // manually create N_down
        LinkedList<Double> try_N_down = new LinkedList<Double>();

        // link a cur_time = 2 params_time = 2
    	/*
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	*/

        // link a cur_time = 5 params_time = 5 normal condition
    	/*
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	*/

        // link a cur_time = 5 params_time = 5 two break point condition checking page-57
    	/*
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	*/


        // link a cur_time = 8 params_time = 8
    	/*
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	*/


        // link a cur_time = 9 params_time = 9
    	/*
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 0);
    	try_N_down.add((double) 4);
    	*/


        // link a cur_time = 10 params_time = 10
    	/*
    	try_N_down.add((double) 0); // 2
    	try_N_down.add((double) 0); // 3
    	try_N_down.add((double) 0); // 4
    	try_N_down.add((double) 0); // 5
    	try_N_down.add((double) 0); // 6
    	try_N_down.add((double) 0); // 7
    	try_N_down.add((double) 0); // 8
    	try_N_down.add((double) 4); // 9
    	try_N_down.add((double) 8); // 10
    	*/



        // link a cur_time = 11 params_time = 11
    	/*
    	try_N_down.add((double) 0); // 3
    	try_N_down.add((double) 0); // 4
    	try_N_down.add((double) 0); // 5
    	try_N_down.add((double) 0); // 6
    	try_N_down.add((double) 0); // 7
    	try_N_down.add((double) 0); // 8
    	try_N_down.add((double) 4); // 9
    	try_N_down.add((double) 8); // 10
    	try_N_down.add((double) 12); // 11
    	*/

        // link a cur_time = 12 params_time = 12
    	/*
    	try_N_down.add((double) 0); // 4
    	try_N_down.add((double) 0); // 5
    	try_N_down.add((double) 0); // 6
    	try_N_down.add((double) 0); // 7
    	try_N_down.add((double) 0); // 8
    	try_N_down.add((double) 4); // 9
    	try_N_down.add((double) 8); // 10
    	try_N_down.add((double) 12); // 11
    	try_N_down.add((double) 16); // 12
    	*/


        // link a cur_time = 13 params_time = 13
    	/*
    	try_N_down.add((double) 0); // 5
    	try_N_down.add((double) 0); // 6
    	try_N_down.add((double) 0); // 7
    	try_N_down.add((double) 0); // 8
    	try_N_down.add((double) 4); // 9
    	try_N_down.add((double) 8); // 10
    	try_N_down.add((double) 12); // 11
    	try_N_down.add((double) 16); // 12
    	try_N_down.add((double) 16); // 13
    	*/


        // ---> for link b
        // link b cur_time = 13 params_time = 13
    	/*
    	try_N_down.add((double) 0); // 5
    	try_N_down.add((double) 0); // 6
    	try_N_down.add((double) 0); // 7
    	try_N_down.add((double) 0); // 8
    	try_N_down.add((double) 0); // 9
    	try_N_down.add((double) 0); // 10
    	try_N_down.add((double) 0); // 11
    	try_N_down.add((double) 0); // 12
    	try_N_down.add((double) 0); // 13
    	*/


        // link b cur_time = 15 params_time = 15
    	/*
    	try_N_down.add((double) 0); // 7
    	try_N_down.add((double) 0); // 8
    	try_N_down.add((double) 0); // 9
    	try_N_down.add((double) 0); // 10
    	try_N_down.add((double) 0); // 11
    	try_N_down.add((double) 0); // 12
    	try_N_down.add((double) 0); // 13
    	try_N_down.add((double) 0); // 14
    	try_N_down.add((double) 0); // 15
    	*/


        // link b cur_time = 17 params_time = 17
    	/*
    	try_N_down.add((double) 0); // 9
    	try_N_down.add((double) 0); // 10
    	try_N_down.add((double) 0); // 11
    	try_N_down.add((double) 0); // 12
    	try_N_down.add((double) 0); // 13
    	try_N_down.add((double) 0); // 14
    	try_N_down.add((double) 0); // 15
    	try_N_down.add((double) 0); // 16
    	try_N_down.add((double) 4); // 17
    	*/

        // -------------------------------------- getPressure() testing N_down for link a & link b -------------------------------------- //
        LinkedList<Double> try_N_down_linka = new LinkedList<Double>();
        LinkedList<Double> try_N_down_linkb = new LinkedList<Double>();

        // link a & link b cur_time = 5 params_time = 5 normal condition
    	/*
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 0);

    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	*/

        // link a & link b cur_time = 10 params_time = 10 normal condition
    	/*
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 4);
    	try_N_down_linka.add((double) 8);

    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	*/


        // link a & link b cur_time = 11 params_time = 11 normal condition

        try_N_down_linka.add((double) 0);
        try_N_down_linka.add((double) 0);
        try_N_down_linka.add((double) 0);
        try_N_down_linka.add((double) 0);
        try_N_down_linka.add((double) 0);
        try_N_down_linka.add((double) 0);
        try_N_down_linka.add((double) 4);
        try_N_down_linka.add((double) 8);
        try_N_down_linka.add((double) 12);

        try_N_down_linkb.add((double) 0);
        try_N_down_linkb.add((double) 0);
        try_N_down_linkb.add((double) 0);
        try_N_down_linkb.add((double) 0);
        try_N_down_linkb.add((double) 0);
        try_N_down_linkb.add((double) 0);
        try_N_down_linkb.add((double) 0);
        try_N_down_linkb.add((double) 0);
        try_N_down_linkb.add((double) 0);


        // link a & link b cur_time = 13 params_time = 13 normal condition
    	/*
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 0);
    	try_N_down_linka.add((double) 4);
    	try_N_down_linka.add((double) 8);
    	try_N_down_linka.add((double) 12);
    	try_N_down_linka.add((double) 16);
    	try_N_down_linka.add((double) 16);

    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	try_N_down_linkb.add((double) 0);
    	*/



        System.out.println("N_down is:" + try_N_down);
        System.out.println("N_down size is: " + try_N_down.size());

        /* Function Testing !!!! */
    	/* 注意一下 params_time 和 cur_time 都要改动
    	   cur_time 如果是double 呢 ？？？？ ---> 应该不能是double 吧 因为simulation time 都是整数
    	   需要统一下数据的精度问题
    	 */


        // ------> Testing ShockWaveDetection3() return x_s

        // for link a
        //LinkedList<Double> try_xs_solution10 = LTM.ShockWaveDetection3(10, 10, try_N_up, try_N_down); // change ShockWaveDetection2 to static
        //System.out.println("The shock wave position x_s for demo code is:" + try_xs_solution10);

        //LinkedList<Double> try_xs_solution11 = LTM.ShockWaveDetection3(11, 11, try_N_up, try_N_down); // change ShockWaveDetection2 to static
        //System.out.println("The shock wave position x_s for demo code is:" + try_xs_solution11);

        //LinkedList<Double> try_xs_solution13 = LTM.ShockWaveDetection3(13, 13, try_N_up, try_N_down); // change ShockWaveDetection2 to static
        //System.out.println("The shock wave position x_s for demo code is:" + try_xs_solution13); // ---> result should be same as t = 5

        //for link b
        //LinkedList<Double> try_xs_solution13 = LTM.ShockWaveDetection3(13, 13, try_N_up, try_N_down);
        //System.out.println("The shock wave position x_s for link b is:" + try_xs_solution13);


        // ------> Testing shockN() return x_s corresponding N
        // for link a
        //LinkedList<Double> try_x_shock = new LinkedList<Double>();
        //try_x_shock.add(0.1399999999999999);  // x_s = 0.14999 不能取 x_s = 0.15
        //LinkedList<Double> try_x_shock_N_solution = LTM.shockN(10, try_x_shock, try_N_up, try_N_down);
        //System.out.println("The shock x_s corresponding N is:" + try_x_shock_N_solution); // ---> x_shock = 0.14 cur_time = 10 check page-58: correct answer is 14.4 & return result is 14.3999 还是有精度问题

        // for link b
        //LinkedList<Double> try_x_shock = new LinkedList<Double>();
        //try_x_shock.add(0.175);
        //LinkedList<Double> try_x_shock_N_solution = LTM.shockN(13, try_x_shock, try_N_up, try_N_down);
        //System.out.println("The shock x_s corresponding N for link b is:" + try_x_shock_N_solution); // cut_time = 12


        // ------> Testing downstreamBreakpointDetection() return x_break_downstream
        // for link a
        // public static LinkedList<Double> downstreamBreakpointDetection(int cur_time2, int params_time, LinkedList<Double> N_up2, LinkedList<Double> N_down2)
        //LinkedList<Double> try_x_break_downstream = LTM.downstreamBreakpointDetection(10, 10, try_N_up, try_N_down);
        //LinkedList<Double> try_x_break_downstream = LTM.downstreamBreakpointDetection(11, 11, try_N_up, try_N_down);
        //LinkedList<Double> try_x_break_downstream = LTM.downstreamBreakpointDetection(13, 13, try_N_up, try_N_down); // result is 0.075 但是
        //System.out.println("The backward break point from downstream for demo code is:" + try_x_break_downstream);
        // cur_time  = 10 results: [0.15000000000000002]  // cur_time  = 13 results: [0.075000001] 这个是无效解 因为 < x_b
        // for link b
        //LinkedList<Double> try_x_break_downstream = LTM.downstreamBreakpointDetection(13, 13, try_N_up, try_N_down);
        //System.out.println("The backward break point from downstream for demo code is:" + try_x_break_downstream);


        // ------> Testing backwardBreakN() return x_break_downstream_N;
        //LinkedList<Double> try_x_break_downstream = new LinkedList<Double>();
        //try_x_break_downstream.add(0.15000000000000002);
        //LinkedList<Double> try_x_break_downstream_N = LTM.backwardBreakN(13, try_x_break_downstream, try_N_up,  try_N_down); // Index = 3
        //System.out.println("The backward break point N demo code is:" + try_x_break_downstream_N);

        // Testing Math.round
        //System.out.println( Math.round (0.99999999999999964) ); // -> 1
        //System.out.println( Math.round (0.1399999999999999) ); // -> 0
        //System.out.println( Math.round (5.999999999999999964) ); // -> 6
        //System.out.println( Math.round (13.999999999999999964) ); // -> 14


        // ------> Testing upstreamBreakpointDetection() return x_break_upstream
        // for link a
        //LinkedList<Double> try_x_break_upstream = LTM.upstreamBreakpointDetection(5, 5, try_N_up, try_N_down);
        //System.out.println("The upstream break point from upstream for demo code is:" + try_x_break_upstream); // results is is:[0.15000000000000002, 0.05]
        // for link b
        //LinkedList<Double> try_x_break_upstream = LTM.upstreamBreakpointDetection(13, 13, try_N_up, try_N_down);
        //System.out.println("The upstream break point from upstream for demo code is:" + try_x_break_upstream);

        // ------> Testing upstreamBreakpointDetection() return x_break_upstream_N
        // for link a
        //double try_x_break_upstream_N = LTM.forwardBreakN(5, 0.150000000001, try_N_up, try_N_down); // 0.15000000000000002 输入 return 值有问题  需要解决精度问题  先让他向下整数？？因为想
        //System.out.println("The upstream break point N from upstream for demo code is:" + try_x_break_upstream_N);
        // for link b
        //double try_x_break_upstream_N = LTM.forwardBreakN(13, 0.05, try_N_up, try_N_down);
        //System.out.println("The upstream break point N from upstream for demo code is:" + try_x_break_upstream_N);


        // ------> Testing get_X_N_pairs() ------> Testing case: link a & t = 2 (上面是空的情况);  link a  & t  = 5 on page-57（多个upstream); link a & t = 10; link a & t = 11; link b & t = 13 (下面是空的);  link b & t = 15 (下面是空的);
        // for link a
        //HashMap<Double, Double> try_X_N_pairs_link_a = LTM.get_X_N_pairs(2, 2, try_N_up_linka, try_N_down_linka);
        //HashMap<Double, Double> try_X_N_pairs_link_a = LTM.get_X_N_pairs(5, 5, try_N_up_linka, try_N_down_linka);
        //HashMap<Double, Double> try_X_N_pairs_link_a = LTM.get_X_N_pairs(8, 8, try_N_up_linka, try_N_down_linka);
        //HashMap<Double, Double> try_X_N_pairs_link_a = LTM.get_X_N_pairs(9, 9, try_N_up_linka, try_N_down_linka);
        //HashMap<Double, Double> try_X_N_pairs_link_a = LTM.get_X_N_pairs(10, 10, try_N_up_linka, try_N_down_linka);
        //HashMap<Double, Double> try_X_N_pairs_link_a = LTM.get_X_N_pairs(11, 11, try_N_up_linka, try_N_down_linka);
        //HashMap<Double, Double> try_X_N_pairs_link_a = LTM.get_X_N_pairs(12, 12, try_N_up_linka, try_N_down_linka);
        //HashMap<Double, Double> try_X_N_pairs_link_a = LTM.get_X_N_pairs(13, 13, try_N_up_linka, try_N_down_linka);

        // for link b
        // TODO:
        //HashMap<Double, Double> try_X_N_pairs_link_b = get_X_N_pairs(13, 13, try_N_up_linka, try_N_down_linka);
        //HashMap<Double, Double> try_X_N_pairs_link_b = LTM.get_X_N_pairs(15, 15, try_N_up_linka, try_N_down_linka);
        //HashMap<Double, Double> try_X_N_pairs_link_b = LTM.get_X_N_pairs(17, 17, try_N_up_linka, try_N_down_linka); // works perfect


        // ------> Testing get_density() ------> Testing case on google docs:
        // for link a
        //ArrayList<Double> try_get_density_link_a =  getDensity(try_X_N_pairs_link_a, 5, 5, try_N_up, try_N_down);
        //System.out.println("Tesing get_density function, result is:" + try_get_density_link_a);

        // for link b
        //ArrayList<Double> try_get_density_link_b =  getDensity(try_X_N_pairs_link_b, 13, 13, try_N_up, try_N_down);
        //System.out.println("Testing get_density function, result is:" + try_get_density_link_b);


        // ------> Testing Sorted HashMap function
    	/*
    	HashMap<Double, Double> test_unsorted_X_N_pairs = new HashMap<>();
    	test_unsorted_X_N_pairs.put((double)0.00, (double) 24.00);
    	test_unsorted_X_N_pairs.put((double)0.10, (double) 20.00);
    	test_unsorted_X_N_pairs.put((double)0.15, (double) 16.00);
    	test_unsorted_X_N_pairs.put((double)0.20, (double) 12.00);

    	HashMap<Double, Double> test_sorted_X_N_pairs = LTM.sortbykey(test_unsorted_X_N_pairs);


    	ArrayList<Double> test_sortedKeys = new ArrayList<Double>(test_sorted_X_N_pairs.keySet());

    	Collections.sort(test_sortedKeys);
    	//Collections.sort(test_sortedKeys, Collections.reverseOrder());
    	List<Double> test_indexes = new ArrayList<Double>(test_sortedKeys);

    	System.out.println("List of X_N_pairs indexes is :" + test_indexes);
    	System.out.println("Checking HashMap index order :" + test_sorted_X_N_pairs.get((double)0.1)); // check 一下 hashmap 存数据的顺利 对应的值
    	*/


        // ------> Testing getPressure_normal() function
        LTM upLink = null;
        LTM downLink = null;
        //HashMap<Double, Double> try_X_N_pairs_link_a = LTM.get_X_N_pairs(10, 10, try_N_up_linka, try_N_down_linka);
        //HashMap<Double, Double> try_X_N_pairs_link_b = LTM.get_X_N_pairs(10, 10, try_N_up_linkb, try_N_down_linkb);

        double try_getPressure_value = LTM.getPressure_normal(upLink, downLink, 0.5, 0.5, 1.0, 11, 11, try_N_up_linka, try_N_up_linkb, try_N_down_linka, try_N_down_linkb);



        // ------> Testing for loop:
    	/*
    	for (int i = 2;  i < 3; i++ ) {
    		int y = 0;
    		System.out.println("Testing for loop :" + y);
    	}
    	*/



    }


}

