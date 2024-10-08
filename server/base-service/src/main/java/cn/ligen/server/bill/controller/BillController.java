package cn.ligen.server.bill.controller;

import cn.ligen.server.bill.entity.po.BillEntity;
import cn.ligen.server.bill.entity.dto.BillDto;
import cn.ligen.server.bill.entity.mapper.BillEntityStruct;
import cn.ligen.server.bill.entity.mapper.BillEntityStructImpl;
import cn.ligen.server.bill.entity.query.BillQuery;
import cn.ligen.server.bill.entity.vo.BillDetailVo;
import cn.ligen.server.bill.entity.vo.BillVo;
import cn.ligen.server.bill.service.BillService;
import cn.ligen.server.common.util.CommonPage;
import cn.ligen.server.common.util.CommonResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author ligen
 * @date 2023/9/2 23:32
 * @description
 */
@Slf4j
@RestController
@Tag(name = "账单管理")
@RequestMapping("/api/bill")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @Operation(summary = "添加账单信息")
    @PostMapping("/add")
    @Parameters({
            @Parameter(name = "title", description = "账单名称", required = true),
            @Parameter(name = "code", description = "账单类型", required = true),
            @Parameter(name = "costTime", description = "账单花费时间", required = true),
            @Parameter(name = "cost", description = "账单花费金额", required = true),
    })
    public CommonResult<Object> addBill(@Validated({BillDto.Add.class}) @RequestBody BillDto dto) {
        BillEntity entity = BillEntityStruct.INSTANCE.toEntity(dto);
        Integer cnt = billService.addBill(entity, dto.getBillBooks());
        return CommonResult.success(null);
    }

    @Operation(summary = "批量导入账单信息")
    @PostMapping("/import")
    @Parameters({
            @Parameter(name = "title", description = "账单名称", required = true),
            @Parameter(name = "code", description = "账单类型", required = true),
            @Parameter(name = "costTime", description = "账单花费时间", required = true),
            @Parameter(name = "cost", description = "账单花费金额", required = true)
    })
    public CommonResult<Object> importBills(@Validated({BillDto.Add.class}) @RequestBody List<BillDto> dto) {
        List<BillEntity> billList = new ArrayList<>(dto.size());
        for (BillDto billDto : dto) {
            billList.add(BillEntityStruct.INSTANCE.toEntity(billDto));
        }
        Integer cnt = billService.importBillList(billList);
        return CommonResult.success(cnt);
    }

    @Operation(summary = "修改账单")
    @PostMapping("/update")
    @Parameters({
            @Parameter(name = "id", description = "账单id", required = true),
            @Parameter(name = "title", description = "账单名称", required = true),
            @Parameter(name = "code", description = "账单类型", required = true),
            @Parameter(name = "costTime", description = "账单花费时间", required = true),
            @Parameter(name = "cost", description = "账单花费金额", required = true),
    })
    public CommonResult<Object> billUpdate(@Validated({BillDto.Update.class}) @RequestBody BillDto dto) {
        BillEntity entity = BillEntityStructImpl.INSTANCE.toEntity(dto);
        billService.updateBill(entity, dto.getBillBooks());
        return CommonResult.success();
    }

    @Operation(summary = "删除账单")
    @PostMapping("/delete")
    @Parameters({
            @Parameter(name = "ids", description = "要删除账单列表", required = true),
    })
    public CommonResult<Object> billDelete(@RequestBody BillDto dto) {
        billService.deleteBill(dto.getIds());

        return CommonResult.success();
    }

    @Operation(summary = "获取账单列表")
    @GetMapping("/list")
    @Parameters({
            @Parameter(name = "codes", description = "账单种类编号", required = true),
            @Parameter(name = "startTime", description = "账单花费时间", required = true),
            @Parameter(name = "endTime", description = "账单花费时间", required = true),
            @Parameter(name = "lowCost" ,description = "账单花费金额", required = true),
            @Parameter(name = "highCost" ,description = "账单花费金额", required = true)
    })
    public CommonResult<CommonPage<BillVo>> billList(BillQuery query, Page<BillEntity> page) {
        List<BillEntity> billEntities = billService.queryBillList(query, page);
        List<BillVo> billVos = new ArrayList<>();
        for (BillEntity billEntity : billEntities) {
            billVos.add(BillEntityStruct.INSTANCE.toVo(billEntity));
        }
        CommonPage<BillVo> commonPage = new CommonPage<>();
        commonPage.setSize(page.getSize());
        commonPage.setTotal(page.getTotal());
        commonPage.setRecords(billVos);
        commonPage.setCurrent(page.getCurrent());
        return CommonResult.success(commonPage);
    }

    @Operation(summary = "获取账单详情")
    @GetMapping("/detail")
    public CommonResult<BillDetailVo> queryBillDetail(Integer id) {
        return CommonResult.success(billService.queryBillDetail(id));
    }

    @Operation(summary = "统计账单")
    @GetMapping("/stat")
    @Parameters({
            @Parameter(name = "codes", description = "账单种类编号", required = true),
            @Parameter(name = "startTime", description = "账单花费时间", required = true),
            @Parameter(name = "endTime", description = "账单花费时间", required = true),
            @Parameter(name = "lowCost" ,description = "账单花费金额", required = true),
            @Parameter(name = "highCost" ,description = "账单花费金额", required = true)
    })
    public CommonResult<Object> billStatistic(BillQuery query) {
        // 前端请求默认今年来
        // 也可具体选时间，由前端自己生成时间
        return CommonResult.success(billService.billStat(query));
    }
}
